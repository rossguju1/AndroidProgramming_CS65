import csv
import re 
import json

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
