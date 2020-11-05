import tensorflow
import tensorflow as tf
class Driver:
  def __init__(self, window, strategy,metrics,model_path=None, convert_lite = False):
    self.window = window
    self.path = model_path

    if metrics =='auc':
        self.metrics = tf.keras.metrics.AUC(name='auc')
    elif metrics =='acc':
        self.metrics = tensorflow.keras.metrics.BinaryAccuracy(name='accuracy', threshold =0.5)
    elif metrics =='precision':
        self.metrics = tf.keras.metrics.Precision(name='precision')
    elif metrics =='recall':
        self.metrics = tf.keras.metrics.Recall(name='recall')
    
    self.loss = tensorflow.keras.lossess.binarycrossentropy()
    self.optimiser = tensorflow.keras.optimizers.Adam(lr = 1e-4)
    self.convertmodel = convert_lite

    if strategy == 'InceptionTime':
      self.model = self.Incep()
    else:
      self.model = self.Wave()
    
    def block(self, layer):
      x1 = tensorflow.keras.layers.Conv1D(16, 1, padding='same', activation='relu')(self.layer)

      x2 = tensorflow.keras.layers.Conv1D(16, 1, padding='same', activation='relu')(self.layer)
      x2 = tensorflow.keras.layers.Conv1D(32, 3, padding='same', activation='relu')(x2)

      x3 = tensorflow.keras.layers.Conv1D(16, 1, padding='same', activation='relu')(self.layer)
      x3 = tensorflow.keras.layers.Conv1D(32, 5, padding='same', activation='relu')(x3)

      output  = tensorflow.concat([x1,x2,x3,self.layer], axis =  -1)

      return output

    def Incep(self):
        history_seq = tensorflow.keras.layers.Input(shape=(self.window, 5))
        x = history_seq
        x = tensorflow.keras.layers.Conv1D(16, 1, activation='relu')(x)
        x = tensorflow.keras.layers.AveragePooling1D(2)(x)
        x = tensorflow.keras.layers.Conv1D(16, 1, activation='relu')(x)
        x = tensorflow.keras.AveragePooling1D(2)(x)
        x = self.block(x)
        x = tensorflow.keras.AveragePooling1D(2)(x)
        x = self.block(x)
        x = tensorflow.keras.layers.Conv1D(16, 1, padding='same', activation='relu')(x)
        x = tensorflow.keras.Flatten()(x)
        x = tensorflow.keras.Dense(50, activation='relu')(x)
        x = tensorflow.keras.Dropout(0.2)(x)
        x = tensorflow.keras.Dense(1, activation='sigmoid')(x)

        model = tensorflow.keras.models.Sequential(history_seq,x)
        model.compile(loss=self.loss, optimizer=self.optimizer, metrics = self.metrics)
        return model
    
    def Wave(self, n_filters =32, filter_width = 10 , dilation_rates =[2**i for i in range(8)] * 2 ):

        history_seq = tensorflow.keras.layers.Input(shape=(self.window, 5))
        x = history_seq

        skips = []
        for dilation_rate in dilation_rates:
            
            # preprocessing - equivalent to time-distributed dense
            x = tensorflow.keras.layers.Conv1D(16, 1, padding='same', activation='relu')(x) 
            
            # filter convolution
            x_f = tensorflow.keras.layers.Conv1D(filters=n_filters,
                        kernel_size=filter_width, 
                        padding='causal',
                        dilation_rate=dilation_rate)(x)
            
            # gating convolution
            x_g = tensorflow.keras.layers.Conv1D(filters=n_filters,
                        kernel_size=filter_width, 
                        padding='causal',
                        dilation_rate=dilation_rate)(x)
            
            # multiply filter and gating branches
            z = tensorflow.keras.layers.Multiply()([tensorflow.keras.layers.Activation('tanh')(x_f),
                            tensorflow.keras.layers.Activation('sigmoid')(x_g)])
            
            # postprocessing - equivalent to time-distributed dense
            z = tensorflow.keras.layers.Conv1D(16, 1, padding='same', activation='relu')(z)
            
            # residual connection
            z = tensorflow.keras.layers.Add()([x, z])    
            
            # collect skip connections
            skips.append(z)

        # add all skip connection outputs 
        out = tensorflow.keras.layers.Activation('relu')(tensorflow.keras.layers.Add()(skips))

        # final time-distributed dense layers 
        out = tensorflow.keras.layers.Conv1D(100, 1, padding='same')(out)
        out = tensorflow.keras.layers.Activation('relu')(out)
        out = tensorflow.keras.layers.Dropout(.2)(out)
        out = tensorflow.keras.layers.Conv1D(1,1, padding='same')(out)
        out = tensorflow.keras.layers.Flatten()(out)
        out = tensorflow.keras.layers.Dense(100, activation='relu')(out)
        out = tensorflow.keras.layers.Dropout(.3)(out)
        out = tensorflow.keras.layers.Dense(10, activation='relu')(out)
        out = tensorflow.keras.layers.Dense(1, activation='sigmoid')(out)

        model = tensorflow.keras.models.Sequential(history_seq,out)
        model.compile(loss=self.loss, optimizer=self.optimizer, metrics = self.metrics)
        return model
        
    def save(self):
      tensorflow.saved_model.save(self.model,self.path) 