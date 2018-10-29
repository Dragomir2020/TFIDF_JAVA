import java.io.*;
import java.lang.*;
import java.util.*;

class Entry {

	private Corpus blogCorp;
	
	// Entrance for code execution
    public static void main(String[] args) {
	
		Entry enter = new Entry("blog.txt",10000);
		//enter.printTFIDF(2);
		
		for(int i = 0; i < enter.blogCorp.docNumber(); i++){
			TreeMap<Float, String> tfidfs = enter.blogCorp.getTFIDFMapInOrder(i);
			Float Key = tfidfs.lastKey();
			System.out.println("Word: " + tfidfs.get(Key) + " TFIDF: " + Key);
		}
		
    }
	
	// Entry Constructor
	public Entry(String filePath, int corpSize){
		
		String blogs = readTxtFile(filePath);
		
		blogCorp = new Corpus(corpSize);
		
		if(blogs != null){
			String[] blogEntry = blogs.split("/n");
			for(String blog : blogEntry){
				blogCorp.addDocument(blog);
			}	
		}	
			
	}
	
	public void printTFIDF(int Index){
	
		String[] w = blogCorp.getDocument(Index).getWordArray();
		
		for(String wo : w){
			System.out.println("tfidf (" + wo + ") " + Float.toString(blogCorp.tfidf(wo, Index)));
		}
	
	}
	
	public String readTxtFile(String fileName){

        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
			
			String Now = "";
            while((line = bufferedReader.readLine()) != null) {
				Now = Now + line;
            }   

            // Always close files.
            bufferedReader.close();
			 
			return Now;      
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'"); 
			return line;
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
			return line;
        }
	
	}
	
}


// This class is utilized to keep track of the word frequency for idf calculations
class Corpus {
	
	private int numberOfDocuments;
	private Map<String, Integer> termFrequency;
	private List<CorpusDocument> documents;

	//Constructor
	public Corpus(int corpusSize){
		termFrequency = new HashMap<String, Integer>(corpusSize);
		documents = new ArrayList<CorpusDocument>();
		numberOfDocuments = 0;
	}
	
	public void addDocument(String sentance){
		numberOfDocuments++;
		CorpusDocument docNow = new CorpusDocument(sentance);
		documents.add(docNow);
		
		Map<String, Integer> wordFreq  = docNow.getWordFrequency();
		
		// Gets the unique words from document and saves as the document containing word
		for(Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
			String term = entry.getKey();
			
			if(termFrequency.containsKey(term)){
				// Already has word
				termFrequency.put(term, termFrequency.get(term) + 1);
			}else{
				// New word not previously seen
				termFrequency.put(term, 1);
			}
			
		}
		
	}
	
	public CorpusDocument getDocument(int index){
		return documents.get(index);
	}
	
	public int docNumber(){
		return numberOfDocuments;
	}
	
	private int docsContainingTerm(String term){
		if(termFrequency.containsKey(term)){
			// Already has word
			return termFrequency.get(term);
		}else{
			// Don't return zero bc divide by zero
			return 0;
		}
	}
	
	// For this what is considered a document must be considered
	// I am going to consider blog content and each comment as a seperate document for now
	public float idf(String term){
		return (float)Math.log(((double)numberOfDocuments/(double)docsContainingTerm(term)) + (double)1);
	}
	
	public float tfidf(String word, int docNumber){
		return documents.get(docNumber).getTF(word) * idf(word);
	}
	
	// Returs=ns unordered hashmap
	public Map<String, Float> getTFIDFMap(int docNumber){
	
		String[] w = this.getDocument(docNumber).getWordArray();
		Map<String, Float> tfidf = new HashMap<String, Float>(w.length);
		
		for(String wo : w){
			 tfidf.put(wo, this.tfidf(wo, docNumber));
		}
		
		return tfidf;
	}
	
	// Returns balanced TreeMap in order
	public TreeMap<Float, String> getTFIDFMapInOrder(int docNumber){
	
		String[] w = this.getDocument(docNumber).getWordArray();
		TreeMap<Float, String> tfidf = new TreeMap<Float, String>();	
		for(String wo : w){
			 tfidf.put(this.tfidf(wo, docNumber), wo);
		}
		return tfidf;
	}
	
	// Holds individual documents for term frequency
	class CorpusDocument {

		//Array of words parsed out of string passed in
		private String[] words;
		private String docSentance;
		private Map<String, Integer> wordFrequency;
		private int numberOfWords;
		
		// Use these as stopwords
		private String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};
		private Set<String> stopWordSet = new HashSet<String>(Arrays.asList(stopwords));
		private Set<String> stemmedStopWordSet = stemStringSet(stopWordSet);
		
		
		//Constructor
		public CorpusDocument(String sentance){
			numberOfWords = 0;
			wordFrequency = new HashMap<String, Integer>();
			docSentance = sentance;
			//String stemmed = stemString(sentance);
			//String noStop = removeStemmedStopWords(sentance);
			parseWords(stemString(sentance)); // Where should I stem string
			countWords();
		}
	
		public void setWords(String sentance){
			numberOfWords = 0;
			docSentance = sentance;
			wordFrequency.clear();
			String noStop = removeStemmedStopWords(sentance);
			parseWords(noStop);
			countWords();
		}
		
		private String removeStopWords(String string) {
			String result = "";
			String[] words = string.toLowerCase().split("\\W+");
			for(String word : words) {
				if(word.isEmpty()) continue;
				if(isStopword(word)) continue; //remove stopwords
				result += (word+" ");
			}
			return result;
		}
		
		private String removeStemmedStopWords(String string) {
			String result = "";
			String[] words = string.split("\\W+");
			for(String word : words) {
				if(word.isEmpty()) continue;
				if(isStemmedStopword(word)) continue;
				if(word.charAt(0) >= '0' && word.charAt(0) <= '9') continue; //remove numbers, "25th", etc
				result += (word+" ");
			}
			return result;
		}
		
		private boolean isStopword(String word) {
			if(word.length() < 2) return true;
			if(word.charAt(0) >= '0' && word.charAt(0) <= '9') return true; //remove numbers, "25th", etc
			for(String stop : stopwords){
				if(word.equals(stop.toLowerCase())) return true;
			}
			return false;
		}
		
		private boolean isStemmedStopword(String word) {
			if(word.length() < 2) return true;
			if(word.charAt(0) >= '0' && word.charAt(0) <= '9') return true; //remove numbers, "25th", etc
			String stemmed = stemString(word);
			if(stopWordSet.contains(stemmed.toLowerCase())) return true;
			if(stemmedStopWordSet.contains(stemmed.toLowerCase())) return true;
			if(stopWordSet.contains(word.toLowerCase())) return true;
			if(stemmedStopWordSet.contains(word.toLowerCase())) return true;
			else return false;
		}
		
		private String stemString(String string) {
			return new Stemmer().stem(string);
		}
			
		public Set<String> stemStringSet(Set<String> stringSet) {
			Stemmer stemmer = new Stemmer();
			Set<String> results = new HashSet<String>();
			for(String string : stringSet) {
				results.add(stemmer.stem(string));
			}
			return results;
		}
	
		// Method is used to parse words out of string
		private void parseWords(String sentance){
			words = sentance.toLowerCase().split("\\W+");
		}
		
		private void countWords(){
		
			for(String word : words){
			
				// Add one to the number of words
				numberOfWords++;
				
				if(wordFrequency.containsKey(word)){
					// Already has word
					wordFrequency.put(word, wordFrequency.get(word) + 1);
				}else{
					// New word not previously seen
					wordFrequency.put(word, 1);
				}
				
			}
		}
		
		// Returns given term frequency for a given document
		public float getTF(String word){
			if(wordFrequency.containsKey(word)){
				// Already has word
				return ((float)wordFrequency.get(word) / (float)numberOfWords);
			}else{
				return (float)0;
			}
			
		}
	
		public String[] getWordArray() {
			return words;
		}
		
		public String getSentance() {
			return docSentance;
		}
		
		public int getWordCount() {
			return numberOfWords;
		}
		
		public Map<String, Integer> getWordFrequency() {
			return wordFrequency;
		}
	
	}

}


// This class is utilized to keep track of the word frequency for idf calculations
public class Corpus throws Exception{
	
	private int numberOfDocuments;
	private Map<String, Integer> termFrequency;

	//Constructor
	public Corpus(int corpusSize){
		termFrequency = new HashMap<String, Integer>(corpusSize);
		numberOfDocuments = 0;
	}
	
	//Assume passing in hashmap of the given document
	public void addDocument(CorpusDocument document){
		numberOfDocuments++;
		Map<String, Integer> wordFreq  = document.getWordFrequency();
		// Gets the unique words from document and saves as the document containing word
		for(Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
			String term = entry.getKey();
			if(termFrequency.containsKey(term)){
				// Already has word
				termFrequency.put(term, termFrequency.get(term) + 1);
			}else{
				// New word not previously seen
				termFrequency.put(term, 1);
			}
		}
	}
	
	public void deleteDocument(CorpusDocument document){
		numberOfDocuments--;
		Map<String, Integer> wordFreq  = document.getWordFrequency();
		// Gets the unique words from document and saves as the document containing word
		for(Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
			String term = entry.getKey();
			if(termFrequency.containsKey(term)){
				// Already has word
				if(termFrequency.get(term) > 0){
					termFrequency.put(term, termFrequency.get(term) - 1);
				} else {
					termFrequency.remove(term);
				}
			}else{
				// Should always have the words to remove because they were added
				throw new ArithmeticException("This should never happen: Trying to remove word and could not.");
			}
		}
	}
	
	public int docNumber(){
		return numberOfDocuments;
	}
	
	public double idf(String term){
		return Math.log(((double)numberOfDocuments/(double)docsContainingTerm(term)) + (double)1);
	}
	
}

	// Holds individual documents for term frequency
	class CorpusDocument {

		//Array of words parsed out of string passed in
		private String[] words;
		private String docSentance;
		private Map<String, Integer> wordFrequency;
		private int numberOfWords;
		
		// Use these as stopwords
		private String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};
		private Set<String> stopWordSet = new HashSet<String>(Arrays.asList(stopwords));
		private Set<String> stemmedStopWordSet = stemStringSet(stopWordSet);
		
		
		//Constructor
		public CorpusDocument(String sentance){
			numberOfWords = 0;
			wordFrequency = new HashMap<String, Integer>();
			docSentance = sentance;
			//String stemmed = stemString(sentance);
			//String noStop = removeStemmedStopWords(sentance);
			parseWords(stemString(sentance)); // Where should I stem string
			countWords();
		}
	
		public void setWords(String sentance){
			numberOfWords = 0;
			docSentance = sentance;
			wordFrequency.clear();
			String noStop = removeStemmedStopWords(sentance);
			parseWords(noStop);
			countWords();
		}
		
		private String removeStopWords(String string) {
			String result = "";
			String[] words = string.toLowerCase().split("\\W+");
			for(String word : words) {
				if(word.isEmpty()) continue;
				if(isStopword(word)) continue; //remove stopwords
				result += (word+" ");
			}
			return result;
		}
		
		private String removeStemmedStopWords(String string) {
			String result = "";
			String[] words = string.split("\\W+");
			for(String word : words) {
				if(word.isEmpty()) continue;
				if(isStemmedStopword(word)) continue;
				if(word.charAt(0) >= '0' && word.charAt(0) <= '9') continue; //remove numbers, "25th", etc
				result += (word+" ");
			}
			return result;
		}
		
		private boolean isStopword(String word) {
			if(word.length() < 2) return true;
			if(word.charAt(0) >= '0' && word.charAt(0) <= '9') return true; //remove numbers, "25th", etc
			for(String stop : stopwords){
				if(word.equals(stop.toLowerCase())) return true;
			}
			return false;
		}
		
		private boolean isStemmedStopword(String word) {
			if(word.length() < 2) return true;
			if(word.charAt(0) >= '0' && word.charAt(0) <= '9') return true; //remove numbers, "25th", etc
			String stemmed = stemString(word);
			if(stopWordSet.contains(stemmed.toLowerCase())) return true;
			if(stemmedStopWordSet.contains(stemmed.toLowerCase())) return true;
			if(stopWordSet.contains(word.toLowerCase())) return true;
			if(stemmedStopWordSet.contains(word.toLowerCase())) return true;
			else return false;
		}
		
		private String stemString(String string) {
			return new Stemmer().stem(string);
		}
			
		public Set<String> stemStringSet(Set<String> stringSet) {
			Stemmer stemmer = new Stemmer();
			Set<String> results = new HashSet<String>();
			for(String string : stringSet) {
				results.add(stemmer.stem(string));
			}
			return results;
		}
	
		// Method is used to parse words out of string
		private void parseWords(String sentance){
			words = sentance.toLowerCase().split("\\W+");
		}
		
		private void countWords(){
		
			for(String word : words){
			
				// Add one to the number of words
				numberOfWords++;
				
				if(wordFrequency.containsKey(word)){
					// Already has word
					wordFrequency.put(word, wordFrequency.get(word) + 1);
				}else{
					// New word not previously seen
					wordFrequency.put(word, 1);
				}
				
			}
		}
		
		// Returns given term frequency for a given document
		public float getTF(String word){
			if(wordFrequency.containsKey(word)){
				// Already has word
				return ((float)wordFrequency.get(word) / (float)numberOfWords);
			}else{
				return (float)0;
			}
			
		}
	
		public String[] getWordArray() {
			return words;
		}
		
		public String getSentance() {
			return docSentance;
		}
		
		public int getWordCount() {
			return numberOfWords;
		}
		
		public Map<String, Integer> getWordFrequency() {
			return wordFrequency;
		}
	
	}



// Practice class to better understand the blog and its related comments
class Blog {

	private List<BlogEntry> BlogPosts;
	private String blogName;
	
	// Blog Constructor
	public Blog(String name){
		blogName = name;
		BlogPosts = new ArrayList<BlogEntry>();
	}
	
	public String getName(){
		return blogName;
	}
	
	public void setName(String name){
		blogName = name;
	}
	
	public void addPost(String content){
		BlogEntry blog = new BlogEntry(content);
		BlogPosts.add(blog);
	}
	
	public BlogEntry getBlogPost(int index){
		return BlogPosts.get(index);
	}
	
	public void addComment(int index, String comment){
		BlogPosts.get(index);
	}

	class BlogEntry{	
		// Main blog content
		String contentString;
		Document contentWords;
		// Comments pertaining to content
		List<EntryComments> comments;
		
		public BlogEntry(String content){
			contentString = content;
		}
		
		public String getContent(){
			return contentString;
		}
	
		public void setContent(String name){
			contentString = name;
		}

		class EntryComments {
			String commentString;
			//Document commentWords;
			
			public String getComment(){
				return commentString;
			}
	
			public void setComment(String name){
				commentString = name;
			}
			
		}

	}

}