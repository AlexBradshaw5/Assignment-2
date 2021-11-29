import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Spelling {
    /*
    no duplicate entries. all lowercase.


     */

    class TrieNode{
        String data;
        List<TrieNode> children = new ArrayList<>();

        public TrieNode(String s) {
            data = s;
        }
    }

    private boolean isWord(String test, File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        scanner.nextLine(); //skips word and count
        int count = 1;
        while(scanner.hasNext()){
            String line = scanner.nextLine();
            String first = "";
            for (int i = 0; i < line.length(); i++) {
                String ch = String.valueOf(line.charAt(i));
                if(ch.isBlank()){
                    break;
                }
                first += ch;
            }
            if (first.startsWith(test)) {
                return true;
            }
            count++;
        }
        return false;
    }

    private List<String> recursion(List<String> list, String prefix, TrieNode root, int count, File file) throws FileNotFoundException {
        if(list.size() >= count){ //no case
            return list;
        }
//        if(isWord(prefix, file)){ // sorry i know I shouldn't be doing this but I put so much effort in and it's due in 3 hours)
//            list.add(prefix);
//            //return list;
//        }
        if(root.children.size() == 0){ //base case
            //prefix += root.data;
            if(!list.contains(prefix)){
                list.add(prefix);
            }
            return list;
        }
        else{ //recursive case
            for (int i = 0; i < root.children.size(); i++) { //
                if(list.size() >= count){
                    break;
                }
                prefix += root.children.get(i).data; //add data to prefix
                recursion(list, prefix, root.children.get(i), count, file); //enter children
                prefix = removeLast(prefix); //remove last character
            }
            return list;
        }
    }

    private TrieNode findRoot(String prefix, TrieNode root){
        for (int i = 0; i < prefix.length(); i++) {//iterate through prefix
            for (int j = 0; j < root.children.size(); j++) {//iterate through children of current node
                if(root.children.get(j).data.equals(String.valueOf(prefix.charAt(i)))){ //if a child's data matches the current character of prefix
                    root = root.children.get(j); //follow path of child
                    break; //restart counting children
                }
            }
        }
        return root;
    }

    private List<String> makeList(String prefix, TrieNode root, int count, File file) throws FileNotFoundException {
        List<String> temp = new ArrayList<>();
        while(temp.size() < count){
            TrieNode start = findRoot(prefix, root);
            temp = recursion(temp, prefix, start, count, file);
            prefix = removeLast(prefix);
        }
        while(temp.size() > 5){
            temp.remove(temp.size()-1);
        }
        return temp;
    }

    private String removeLast(String s){
        String newS = "";
        for (int i = 0; i < s.length() - 1; i++) {
            newS += s.charAt(i);
        }
        return newS;
    }

    public List<List<String>> suggest (String token, int count) throws FileNotFoundException {
        boolean longButEasy = false;
        //File file = new File("C:\\Users\\alexb\\Downloads\\misspelling.txt");
        File file = new File("C:\\Users\\alexb\\Downloads\\unigram_freq.txt");
        List<List<String>> suggestions = new ArrayList<>();
        String prefix = "";
        TrieNode root = fillTrie(file);
        for (int i = 0; i < token.length(); i++) {//iterate through token
            prefix += token.charAt(i); //
            System.out.println("Prefix = " + prefix);
            if(longButEasy){
                suggestions.add(easyWay(prefix, count, file));
            }
            else{
                suggestions.add(makeList(prefix, root, count, file));
            }
            System.out.println(suggestions.get(i));
        }


        return suggestions;
    }

    List<String> easyWay(String token, int count, File file) throws FileNotFoundException {
        List<String> suggestions = new ArrayList<>();
        while(suggestions.size() < count){
            Scanner scanner = new Scanner(file);
            scanner.nextLine(); //skips labels
            while(scanner.hasNext()){
                String line = scanner.nextLine();
                String word = "";
                for (int i = 0; i < line.length(); i++) {
                    String ch = String.valueOf(line.charAt(i));
                    if(ch.isBlank()){
                        break;
                    }
                    word += ch;
                }
                if(word.startsWith(token)){
                    if(!suggestions.contains(word)){
                        suggestions.add(word);
                        if(suggestions.size() >= count){
                            return suggestions;
                        }
                    }

                }
            }
            token = removeLast(token);
        }
        return suggestions;
    }

    private TrieNode fillTrie(File file) throws FileNotFoundException {
        TrieNode root = new TrieNode("");
        Scanner scanner = new Scanner(file);
        scanner.nextLine(); //skips word and count
        int testing = 0;
        while(scanner.hasNext()){//loops through each line
            String s = scanner.nextLine(); // s iterates words
            int charInd = 0; //index iterates charcters
            char ch = s.charAt(charInd); //gets first character
            TrieNode currentNode = root;//reset to root

            while(Character.isLetter(ch)){//loops through first type
                boolean canIns = false;
                while(!canIns){ //flag indicates the need to insert char as children, it is known there are no duplicates
                    int i = 0; //i iterates children

                    while(i < currentNode.children.size()){//iterate through children
                        if(currentNode.children.get(i).data.equals(String.valueOf(ch))){ //current character matches one of the children
                            currentNode = currentNode.children.get(i); //follow that child (pause)
                            ch = s.charAt(++charInd);//get next char
                            if(!Character.isLetter(ch)){
                                break; //breaks iterating children
                            }
                            i = 0; //reset loop
                        }
                        else{
                            i++;
                        }
                    }
                    //found no matching characters or white space

                    canIns = true;//ready to insert or breaks loop
                }
                //since we got here there are no more nodes that contain the rest of our word so we need to add them all
                while(Character.isLetter(ch)){//not white space
                    TrieNode newNode = new TrieNode(String.valueOf(ch));//new node with string of character
                    currentNode.children.add(newNode);
                    currentNode = currentNode.children.get(currentNode.children.size()-1);//current node is one we just inserted
                    charInd++; //next character
                    ch = s.charAt(charInd);
                }


                //if not letter while loop of IsLetter ends
            }//gets here is current character isn't letter (word terminates)
            testing++;
        }
        return root;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Spelling test = new Spelling();
        List<List<String>> test2 = test.suggest("peice", 3);
//        for (int i = 0; i < test2.size(); i++) {
//            for (int j = 0; j < test2.get(i).size(); j++) {
//                System.out.println(test2.get(i).get(j));
//            }
//        }
        /*
        For Question 4:
        I quickly noticed that since the algorithm I wrote works with prefixes
        then removes the last letter to work with a smaller prefix, words that
        wouldn't show up in the List<List... would have to be mispelled such that
        there were more than [count] other mispelled words in the same file that
        contained the same starting prefix from where the word in question was
        mispelled. The first word I found that didn't show up in the List was piece
        with a count of 3 since its misspelling was peice so the algorithm
        wouldn't find any pe- and move to p- which contained persistent, pavilion
        and pharaoh. In order to fix this, in this example, one option is to still
        remove the e from p but instead of looking at p-, reshuffle the remaining
        letters (eice) and try again. This would take a long time and not guarantee
        the correct spelling but if it stumbled upon pi- then it would find the correct
        spelling. Another idea is to start looking at the word backward. In this case
        the backwards algorithm would look for -ce in other words that ended in -ce
        but starting at the same starting letter p. This would allow for much
        better accuracy if the word is misspelled in the beginning of the word. In
        the case of really small words (like I just typed on instead of in above),
        an idea is to compare the length of the word with any prefix or suffix. In
        the case where I just typed on, it would recognize the length of 2 and could
        recommend "of" or "in" for example and this would work better for short types
        so the algorithms doesn't go off chasing anything starting with o or ending in i.
         */
    }
}
