import numpy as np
import pandas as pd
import os
import tensorflow as tf
from matplotlib import pyplot as plt

def preprocessing(series,window_size, batch_size):
    ds = tf.data.Dataset.from_tensor_slices((series))
    ds = ds.window(window_size, shift=20, drop_remainder=True)
    ds = ds.flat_map(lambda w: w.batch(window_size))
    ds = ds.shuffle(10000).map(lambda window: (window[:,:-1], window[50,-1:]))
    ds = ds.batch(batch_size).prefetch(1)
    return ds

def train_format(path):
    df = pd.read_csv(path)
    df.columns =['timedate','time','X_acc','Y_acc','Z_acc','X_gyr','Y_gyr','Z_gyr','label']
    df.timedate = pd.to_datetime(df.timedate)
    df['label'] = df.label.apply(lambda x : 0 if x in ['evento_nao_agressivo',0] else 1)
    return df

def test_format(path):
    df = pd.read_csv(path)
    df.columns =['timedate','time','X_acc','Y_acc','Z_acc','X_gyr','Y_gyr','Z_gyr','label']
    df.timedate = pd.to_datetime(df.timedate)
    df['label'] = df.label.apply(lambda x : 0 if x in ['evento_nao_agressivo',0] else 1)
    return df

def get_traindata(window, batch,train_path):
    train = train_format(path)
    data= preprocessing(train[['X_acc','Y_acc','X_gyr','Y_gyr','Z_gyr','label']].values,window,batch)
    return data


def get_testdata(window, batch,test_path):
    test = test_format(path)
    data= preprocessing(test[['X_acc','Y_acc','X_gyr','Y_gyr','Z_gyr','label']].values,window,batch)
    return data

def show_results(history,metrics):
    plt.plot(history.history['{}'.format(metrics)][2:], color ='blue')
    plt.plot(history.history['{}'.format(metrics)][2:], color ='orange')
