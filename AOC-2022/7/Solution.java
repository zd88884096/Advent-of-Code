import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static class Node{
        HashMap<String, Node> sub_dir;
        HashMap<String, Long> files;
        long size;
        Node parent;
        public Node(Node p){
            parent = p;
            sub_dir = new HashMap<>();
            files = new HashMap<>();
            size = 0L;
        }
        public void add_dir(String name){
            if(!sub_dir.containsKey(name)){
                Node node = new Node(this);
                sub_dir.put(name, node);
            }
        }
        public void add_file(String name, long file_size){
            files.put(name, file_size);
        }
        public void print_layout(){
            System.out.println("sub_dir: ");
            print(sub_dir);
            System.out.println("files: ");
            print(files);
            System.out.println("size: " + size);
        }
    }
    //compute the size of each directory after the whole
    //  trie has been built
    //Since we computed the size of all files in each directory
    //  but not the size of all its sub-directories
    //We only need to add size of all sub-dir to each node
    public static long compute_size(Node node){
        //for robustness for avoiding error in recomputation
        node.size = 0L;

        for(String name : node.sub_dir.keySet()){
            Node child = node.sub_dir.get(name);
            node.size += compute_size(child);
        }
        for(String name : node.files.keySet()){
            node.size += node.files.get(name);
        }
        //node.print_layout();
        return node.size;
    }

    //sum size of all directories with size <= lim
    public static long sum_size(Node node, long lim){
        long res = 0L;
        for(String name : node.sub_dir.keySet()){
            Node child = node.sub_dir.get(name);
            res += sum_size(child, lim);
        }
        if(node.size <= lim){
            res += node.size;
        }
        return res;
    }

    //add all node.size to pq
    public static void add_size_to_pq(Node node){
        pq.add(node.size);
        for(String name : node.sub_dir.keySet()){
            add_size_to_pq(node.sub_dir.get(name));
        }
    }

    public static PriorityQueue<Long> pq;
    public static Node root;
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        //idea: Simulate like a Trie, each node is a directory
        //  each node in the trie contains a HashMap of (directory_name, Node) (represent sub-directories),
        //  a HashMap of (file_name, size),
        //  in addition we have fields like parent directory and total size of the directory
        //
        //  Note we also need to keep track of the current directory we are in
        //  when we execute the commands
        String[] arr = read_all_String();
        pq = new PriorityQueue<>();
        //"cd .." at root still gives root
        root = new Node(root);
        Node cur = root;
        int ind = 0;
        while(ind < arr.length){
            String[] toks = split(arr[ind], " ");
            if(toks[1].equals("cd")){
                if(toks[2].equals("/")){
                    cur = root;
                }
                else if(toks[2].equals("..")){
                    cur = cur.parent;
                }
                else{
                    
                    //if no such child recorded yet
                    //  add a child with name toks[2]
                    cur.add_dir(toks[2]);
                    
                    //go one level deeper
                    cur = cur.sub_dir.get(toks[2]);
                }
                ++ind;
            }
            else{
                ++ind;
                while(ind < arr.length && arr[ind].charAt(0) != '$'){
                    toks = split(arr[ind], " ");
                    //print(toks);
                    if(toks[0].equals("dir")){
                        cur.add_dir(toks[1]);
                    }
                    else{
                        cur.add_file(toks[1], Long.parseLong(toks[0]));
                    }
                    ++ind;
                }
            }
        }
        //call such recursion on root would compute sizes for all node in the trie
        compute_size(root);
        //compute_size(root);
        System.out.println("Task 1: " + sum_size(root, 100000));
        add_size_to_pq(root);
        long lower_bound = 30000000L - (70000000L - root.size);
        while((!pq.isEmpty()) && pq.peek() < lower_bound){
            pq.poll();
        }
        System.out.print("Task 2: ");
        if(!pq.isEmpty()){
            System.out.println(pq.peek());
        }
        else{
            System.out.println("You are beautiful");
        }
    }
    

    //template
    public static Scanner sc;
    static long MOD = 1000000007L;
    //(x + dir_card[i], y + dir_card[i + 1]) are 4 cardinal directions
    public static int[] dir_card = {1, 0, -1, 0, 1};
    public static int[] dir_all = {1, 0, -1, 0, 1, -1, -1, 1, 1};

    //add all characters of a String into a HashSet
    public static HashSet<Character> setify(String S){
        HashSet<Character> set = new HashSet<>();
        char[] a = S.toCharArray();
        for(int i = 0; i < a.length; ++i){
            set.add(a[i]);
        }
        return set;
    }
    public static HashSet<Character> setify(char[] a){
        HashSet<Character> set = new HashSet<>();
        for(int i = 0; i < a.length; ++i){
            set.add(a[i]);
        }
        return set;
    }

    //split with multiple delimiters and clearing empty String
    public static String[] split(String S, String delim){
        String[] toks = S.split(delim);
        List<String> l = new ArrayList<>();
        for(String cur : toks){
            if(!cur.equals("")){
                l.add(cur);
            }
        }
        String[] res = new String[l.size()];
        for(int i = 0; i < res.length; ++i){
            res[i] = l.get(i);
        }
        return res;
    }

    //parse a String[] into an int[]
    public static int[] stoi(String[] toks){
        int[] arr = new int[toks.length];
        for(int i = 0; i < toks.length; ++i){
            arr[i] = Integer.parseInt(toks[i]);
        }
        return arr;
    }

    //read all input in the form of a List of String
    public static List<String> read_input(){
        try{
            sc = new Scanner(new File("input.txt"));
        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");        
        }
        List<String> l = new ArrayList<>();
        while(sc.hasNextLine()){
            String S = read();
            l.add(S);
        }
        return l;
    }

    //read all input into a 2d char array
    public static char[][] read_all_char_arr(){
        List<String> l = read_input();
        char[][] arr = new char[l.size()][l.get(0).length()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = l.get(i).toCharArray();
        }
        return arr;
    }
    //read all input into an int array
    public static int[] read_all_int(){
        List<String> l = read_input();
        int[] arr = new int[l.size()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = Integer.parseInt(l.get(i));
        }
        return arr;
    }
    //read all input into a 2d int array
    public static int[][] read_all_int_arr(){
        char[][] arr = read_all_char_arr();
        int row = arr.length, col = arr[0].length;
        int[][] res = new int[row][col];
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                res[i][j] = (int)(arr[i][j] - '0');
            }
        }
        return res;
    }
    //read all input into a String array
    public static String[] read_all_String(){
        List<String> l = read_input();
        String[] arr = new String[l.size()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = l.get(i);
        }
        return arr;
    }


    public static <T> void print(T[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i] + "\t");
        System.out.println();
    }
    public static <T> void print(T[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa[0].length; ++j){
                System.out.print(aa[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }
    public static <T, U> void print(HashMap<T, U> map){
        for(T key : map.keySet()){
            System.out.println(key + " : " + map.get(key));
        }
        System.out.println();
    }
    public static <T> void print(HashSet<T> set){
        System.out.print("{");
        for(T key : set){
            System.out.print(key + ", ");
        }
        System.out.print("}");
        System.out.println();
    }
    public static <T> void print(List<T> list){
        System.out.print("[");
        for(T ele : list){
            System.out.print(ele + ", ");
        }
        System.out.print("]");
        System.out.println();
    }
    public static class Pair<_U, _T> { 
        _U a;
        _T b;
        public Pair(_U ap, _T bp){
            a = ap;
            b = bp;
        }
    }
    public static class Triple<_U, _T, _F>{
        _U a;
        _T b;
        _F c;
        public Triple(_U ap, _T bp, _F cp){
            a = ap;
            b = bp;
            c = cp;
        }
    }
    public static int[] dir = {1, 0, -1, 0, 1, 1, -1, -1, 1};
    public static class Union{
        int[] p, size;
        public Union(int n){
            p = new int[n];
            size = new int[n];
            Arrays.fill(size, 1);
            for(int i = 0; i < n; ++i){
                p[i] = i;
            }
        }
        public int find(int n){
            if(p[n] != n){
                p[n] = find(p[n]);
            }
            return p[n];
        }
        public int union(int a, int b){
            int fa = find(a), fb = find(b);
            if(fa == fb){
                return -1;
            }
            if(size[fa] > size[fb]){
                p[fb] = fa;
                size[fa] += size[fb];
                return size[fa];
            }
            else{
                p[fa] = fb;
                size[fb] += size[fa];
                return size[fb];
            }
        }
    }
    public static String read(){
        return sc.nextLine();
    }
    public static String[] read_toks(){
        return sc.nextLine().split(" ");
    }
}