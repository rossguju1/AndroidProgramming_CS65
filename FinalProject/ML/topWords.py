#!/usr/bin/python

import sys
import nltk
import string
nltk.download('punkt')
nltk.download('stopwords')
from collections import Counter 
from nltk.stem.porter import PorterStemmer
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords 

# opening the text file 
filename = sys.argv[1]
print("Opening: " + filename)
file = open(filename, 'rt')
text = file.read()
file.close()

# Tokenize and stem our words and convert to lowercase
tokens = word_tokenize(text)
tokens = [w.lower() for w in tokens]
# Remove punctuation
table = str.maketrans('', '', string.punctuation)
stripped = [w.translate(table) for w in tokens]
# remove remaining tokens that are not alphabetic
words = [word for word in stripped if word.isalpha()]

# Remove stop words
stop_words = set(stopwords.words('english'))
wordsCleaned = [w for w in words if not w in stop_words]

# porter = PorterStemmer()
# stemmed = [porter.stem(word) for word in words]

# Declare our dictionary
dict = {'init' : 0}

for word in wordsCleaned:
    dict[word] = dict.get(word,0) + 1

k = Counter(dict) 
high = k.most_common(50)

print("Dictionary with 50 highest values:") 
print("Keys: Values") 
  
for i in high: 
    print(i[0]," :",i[1]," ")

cleanedFilename = filename.split('.')[0] + '.csv'
print("Converting to value label pair and saving in: " + cleanedFilename)
print("With Label: " + sys.argv[2])

original_stdout = sys.stdout # Save a reference to the original standard output

with open(filename.split('.')[0] + '.csv', 'w') as f:
    sys.stdout = f # Change the standard output to the file we created.
    for i in high: 
        print(i[0] + ',' + sys.argv[2])
    sys.stdout = original_stdout # ResVet the standard output to its original value
