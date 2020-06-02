import glob, os, sys, string

print("All .csv files in: " + sys.argv[1])
print("Contents being saved to : " + sys.argv[2])
os.chdir(sys.argv[1])
original_stdout = sys.stdout # Save a reference to the original standard output
for file in glob.glob("*.csv"):
    text = open(file,'r').read()
    with open(sys.argv[2], 'a') as f:
        sys.stdout = f # Change the standard output to the file we created.
        print(text)
        sys.stdout = original_stdout # ResVet the standard output to its original value
