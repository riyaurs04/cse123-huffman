// Riya Urs
// CSE 123
// Name of program: Huffman
import java.util.*;
import java.io.*;

// This class represents a Huffman Code that compresses and decompresses data in a file.
public class HuffmanCode {
    private HuffmanNode root;

    // Constructs a new Huffman Code given an array of frequencies
    // Parameters:
    //      - frequencies: an array containing the count of each character in a file, with each
    //        character being represented in the array as an ASCII value
    public HuffmanCode(int[] frequencies) {
        Queue<HuffmanNode> huffQueue = new PriorityQueue<HuffmanNode>();
        for (int i = 0; i < frequencies.length; i++) {
            if(frequencies[i] > 0) {
                HuffmanNode huffNode = new HuffmanNode(frequencies[i], i);
                huffQueue.add(huffNode);
            }
        }
        while(huffQueue.size() > 1) {
            HuffmanNode node1 = huffQueue.remove();
            HuffmanNode node2 = huffQueue.remove();
            HuffmanNode combine = new HuffmanNode(node1.frequency + node2.frequency, node1, node2);
            huffQueue.add(combine);
        }
        this.root = huffQueue.remove();
    }

    // Constructs a new Huffman Code given an input file. For this method, it is assumed that
    // the Scanner is not null and always contains data in the proper standard format. The file
    // format will contain a pair of lines to represent each character in the Huffman code, with
    // the first line being the ASCII code of the character, and the second line being the Huffman
    // encoding for that character.
    // Parameters:
    //      - input: a file containing a previously constructed code
    public HuffmanCode(Scanner input) {
        while(input.hasNextLine()) {
            String asciiStr = input.nextLine();
            int ascii = Integer.parseInt(asciiStr);
            String path = input.nextLine();
            this.root = huffmanCodeHelper(ascii, path, this.root, 0);
        }
    }

    // Constructs a new Huffman Code given an ASCII value (the integer representation of a specific
    // character), a string representation of the path to a specific HuffmanNode, the root node,
    // and an index.
    // Parameters:
    //      - ascii: the integer representation of a specific character
    //      - path: string representation of the path to each root node (e.g. 100, 010, etc.)
    //      - root: the root node of the binary tree
    //      - index: the length of the path to each root node (e.g. 1001 would be 4, 100 would be 
    //        3, etc.). Each time there is a recursive call, the index is incremented by one.
    // Returns:
    //      - HuffmanNode: the root node of the binary tree representing the HuffmanCode
    private HuffmanNode huffmanCodeHelper(int ascii, String path, HuffmanNode root, int index) {
        if(path.length() == index) {
            root = new HuffmanNode(0, ascii);
        } else {
            if(root == null && !path.isEmpty()) {
                root = new HuffmanNode(0, -1);
            }
            if(path.charAt(index) == '0') {
                root.left = huffmanCodeHelper(ascii, path, root.left, index+1);
            } else {
                root.right = huffmanCodeHelper(ascii, path, root.right, index+1);
            }
        } 
        return root;
    }

    // Prints the current Huffman Code to a given output stream in the standard format. The file
    // format will contain a pair of lines to represent each character in the Huffman code, with
    // the first line being the ASCII code of the character, and the second line being the Huffman
    // encoding for that character.
    // Parameters:
    //      - output: the given output stream that the current HuffmanCode is printed to
    public void save(PrintStream output) {
        saveHelper(output, root, "");
    }

    // Prints the current Huffman Code to a given output stream in the standard format given
    // the root node and a string containing a binary representation of the path to each root node.
    // Parameters:
    //      - output: the given output stream that the current HuffmanCode is printed to
    //      - root: the root node in the binary tree representing the HuffmanCode
    //      - code: a string representation of the path to each root node
    private void saveHelper(PrintStream output, HuffmanNode root, String code) {
        if(root != null) {
            if(root.left == null && root.right == null) {
                output.println(root.letter);
                output.println(code);
            } else {
                saveHelper(output, root.left, code + "0");
                saveHelper(output, root.right, code + "1");
            }
        }
    }

    // Reads individual bits from the given input stream and writes the corresponding characters
    // to the given output. Stops reading when the BitInputStream is empty. It is assumed that the
    // input stream contains a valid encoding of characters for this tree’s Huffman Code.
    // Parameters:
    //      - input: the given input stream through which individual bits in a file are read
    //      - output: the given output stream through which individual bits are outputted to
    public void translate(BitInputStream input, PrintStream output) {
        while(input.hasNextBit()) {
            int val = translateHelper(input, root);
            output.write(val);
        }
    }

    // Reads individual bits from the given input stream and writes the corresponding characters
    // to the given output. Stops reading when the BitInputStream is empty. It is assumed that the
    // input stream contains a valid encoding of characters for this tree’s Huffman Code.
    // Parameters:
    //      - input: the given input stream through which individual bits in a file are read
    //      - root: the root node in the binary tree representing the HuffmanCode
    // Returns:
    //      - int: the value of the leaf node
    private int translateHelper(BitInputStream input, HuffmanNode root) {
        while(root.left != null && root.right != null) {
            int val = input.nextBit();
            if(val == 0) {
                root = root.left;
            } else if(val == 1) {
                root = root.right;
            }
        }
        return root.letter;
    }

    // Represents a single node in the tree that is constructed whenever a HuffmanCode object is 
    // created. The HuffmanNode class implements the Comparable interface.
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        public int frequency;
        public int letter;
        public HuffmanNode left;
        public HuffmanNode right;

        // Constructs a new HuffmanNode given a frequency and an ascii value that represents a
        // specific character.
        // Parameters:
        //      - frequency: the number of times a specific character is present in a file
        //      - letter: the ascii value of a specific character
        public HuffmanNode(int frequency, int letter) {
            this.frequency = frequency;
            this.letter = letter;
        }

        // Contructs a new HuffmanNode given a frequency and a left and right node.
        // Parameters:
        //      - frequency: the number of times a specific character is present in a file
        //      - left: the left lead node of the current node
        //      - right: the right leaf node of the current node
        public HuffmanNode(int frequency, HuffmanNode left, HuffmanNode right) {
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        // Compares the current HuffmanNode with another HuffmanNode.
        // Parameters:
        //      - otherNode: a different HuffmanNode that the current HuffmanNode is being
        //        compared to
        // Returns:
        //      - int: returns the HuffmanNode with a greater value. HuffmanNodes with larger
        //        frequencies are returned first. If two HuffmanNodes have the same frequency, 
        //        returns whichever character comes first in the alphabet or is first 
        //        lexicographically.
        @Override
        public int compareTo(HuffmanNode otherNode) {
            return this.frequency - otherNode.frequency;
        }
    }
}
