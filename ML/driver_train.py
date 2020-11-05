import logging
import numpy as np
from tqdm import tqdm
import tensorflow as tf
from driver_utils import preprocessing, train_format, test_format,get_testdata,get_traindata, show_results
from driver_model import Driver

def main(train_path, test_path, batch_size,ep,window, strategy,metrics,model_path=None, convert_lite = False, display = True):

    agent = Driver(window,strategy, metrics,model_path=None, convert_lite = False)

    Xtrain = get_traindata(window, batch_size,train_path)
    Xtest = get_testdata(window,batch_size,test_path)

    model = agent.model

    history = model.fit(Xtrain,  epochs =ep, validation_data=Xtest,verbose = 1,callbacks =[tf.keras.callbacks.EarlyStopping(monitor='val_loss', patience=7)])
    if display:
        show_results(history,metrics)

    if model_path!=None:
        agent.save()
        
    if convert_lite:
        agent.convert()
        
if __name__ == "__main__":

    train_path = "train_GOOG.csv"
    test_path = "val_GOOG_2018.csv"
    strategy = "WaveNet"
    window = 100
    batch_size = 32
    metrics ='auc'
    ep = 100
    debug = False
    try:
        main(train_path, test_path, batch_size, ep,window, strategy,metrics)
    except KeyboardInterrupt:
        print("Aborted!")
