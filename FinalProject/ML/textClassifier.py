import csv
import re 
import json
from sklearn.preprocessing import LabelEncoder
import tensorflow as tf
import numpy as np
from keras.layers import Dense, Input, Dropout
from tensorflow.python.keras import models, optimizers, losses, activations
from keras.layers.normalization import BatchNormalization
from keras.callbacks import EarlyStopping, ModelCheckpoint
from sklearn.model_selection import train_test_split

LE = LabelEncoder()

def train_and_eval(sentences, label, word_index):
    
    # converting categorical labels
    labels = LE.fit_transform(label)
    labels = np.array( labels )
    num_classes = len(labels)
    onehot_labels = tf.keras.utils.to_categorical(labels , num_classes=num_classes)

    setences_tokens = [sentence.split() for sentence in sentences]
    tokenizer = tf.keras.preprocessing.text.Tokenizer()
    tokenizer.word_index = word_index
    sentences_features = tokenizer.texts_to_matrix(setences_tokens)

    train_features, val_features, train_labels, val_labels = train_test_split(sentences_features, onehot_labels, test_size = 0.1)
    feature_input = Input(shape=(sentences_features.shape[1],))
    dense = Dense(128, activation=activations.relu)
    merged =BatchNormalization()(dense)
    merged = Dropout(0.2)(merged)
    merged = Dense(64, activation=activations.relu)(merged)
    merged = BatchNormalization()(merged)
    merged = Dropout(0.2)(merged)
    preds = Dense(num_classes, activation=activations.softmax)(merged)
    # model = tf.keras.Model(inputs=[word_input], outputs=preds)
    model = tf.keras.Model(inputs=[feature_input], outputs=preds)

    model.compile(loss=losses.categorical_crossentropy, optimizer='nadam', metrics=['acc'])

    early_stopping = EarlyStopping(monitor='val_loss', patience=5)
    model.fit([train_features], train_labels,
        validation_data=([val_features], val_labels),
        epochs=200, batch_size=8, shuffle=True,
        callbacks=[early_stopping])
    model.save('models.h5')


sentences , labels = [], []
with open('data.csv','r')as f:
    data = csv.reader(f)
    for row in data:
        sentences.append(row[0])
        labels.append(row[1])

sentences = [ re.sub(r'.,:?{}', ' ', sentence) for sentence in sentences ]

corpus = " ".join(sentences)
words = set(corpus.split())
word_index = {word: index for index, word in enumerate(words)}
with open( 'word_index.json' , 'w' ) as file:
    json.dump( word_index , file )

train_and_eval(sentences, labels, word_index)

def test(sentence, model_path, word_index_path):
 
    classifier = models.load_model( './models.h5' )
    tokenizer = tf.keras.preprocessing.text.Tokenizer(filters='.,:?{} ')
    # sentences = re.sub(r'.,:?{}', ' ', sentence)
    with open(word_index_path, 'r') as f:
        tokenizer.word_index = json.loads(f.read())
        tokenized_messages = tokenizer.texts_to_matrix(sentence.split())
        p = list(classifier.predict(tokenized_messages)[0])
 
        for index, each in enumerate(p):
            print(index, each)

test("Hello math world exponent", './models.h5','./word_inddex.json')
