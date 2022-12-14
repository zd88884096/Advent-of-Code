import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    //get first item in the array s[st..end], doesn't matter if the first item is a list or an integer
    //Returns: Pair of integer, Pair.a = starting index of the next element in s, Pair.b = first item is integer ? value of integer : -1;
    public static Pair<Integer, Integer> first_item(char[] s, int st, int end){
        int ct = 0, ind = st;
        while(ind <= end){
            //get till ct == 0: whole list or just the largest digit of the integer
            if(s[ind] == '['){
                ++ct;
            }
            else if(s[ind] == ']'){
                --ct;
            }
            ++ind;
            if(ct == 0)
                break;
        }
        //first item is integer (otherwise ind is at least 2 greater than st ('[' and ']' of the list takes up 2 characters))
        int res = -1;
        if(ind == st + 1){
            res = 0;
            ind = st;
            while(ind <= end && s[ind] >= '0' && s[ind] <= '9'){
                res *= 10;
                res += s[ind] - '0';
                ++ind;
            }
            
        }
        if(ind > end || s[ind] == ','){
            ++ind;
        }
        return new Pair<>(ind, res);
    }

    //compare two arrays s1[st1..end1] and s2[st2..end2]
    //if(s1[st1..end1] < s2[st2..end2]) return value > 0 (right order)
    //if ... = ... return 0 (equal, right order)
    //if ... > ... return value < 0 (not the right order)
    //edge case: when we get inside "[]" in s, we will automatically have st > end, 
    //  indicating we ran out of element in s, which is in accordance with behavior of the problem (empty list)
    public static int compare(char[] s1, int st1, int end1, char[] s2, int st2, int end2){
        
        //you can check how the comparison works by printing the below lines
        /*
        System.out.println("comparing: " + st1 + " " + end1 + " " + st2 + " " + end2);
        if(st1 <= end1){
            for(int i = st1; i <= end1; ++i){
                System.out.print(s1[i]);
            }
            System.out.println();
        }
        if(st2 <= end2){
            for(int i = st2; i <= end2; ++i){
                System.out.print(s2[i]);
            }
            System.out.println();
        }*/

        //base case: st1 > end1 means we reached the end of s1[st1..end1] (ran out of elements)
        //              so if st2 > end2, both arrays ran out of elements simutaneously, meaning all the previous items are equal,
        //              meaning the two arrays are equal, otherwise the left side is smaller
        if(st1 > end1){
            return st2 > end2 ? 0 : 1;
        }
        //right side is smaller
        if(st2 > end2){
            return -1;
        }
        //neither ran out of elements, compare their first element, if different, return comparison result "comp"
        //  otherwise compare the remaining elements
        Pair<Integer, Integer> p1 = first_item(s1, st1, end1), p2 = first_item(s2, st2, end2);
        int v1 = p1.b, v2 = p2.b, ind1 = p1.a, ind2 = p2.a;
        //start and end indices for the first item in st1 and st2, depends on whether the first element is a list or an integer (-2 for getting back before comma, -3 for getting before comma and ']')
        int st_first_1 = v1 >= 0 ? st1 : st1 + 1, st_first_2 = v2 >= 0 ? st2 : st2 + 1, end_first_1 = v1 >= 0 ? ind1 - 2 : ind1 - 3, end_first_2 = v2 >= 0 ? ind2 - 2 : ind2 - 3;
        int comp = (v1 >= 0 && v2 >= 0) ? v2 - v1 : compare(s1, st_first_1, end_first_1, s2, st_first_2, end_first_2);
        return comp != 0 ? comp : compare(s1, ind1, end1, s2, ind2, end2);
    }
    public static boolean equal(String S, char[] arr){
        StringBuilder sb = new StringBuilder();
        for(char c : arr){
            sb.append(c);
        }
        return S.equals(sb.toString());
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        //idea: Part I: compare items of list recursively
        //  if we are comparing two lists, just take off the brackets of both and compare each element inside, if the first are equal, compare the second, otherwise return comparison result, etc.
        //  if we are comparing a list and an integer, it's equivalent to the problem statement to take off bracket of the list and
        //      compare the first item with the integer, note this holds recursively, i.e. comparing [[3]] with 3, we'll first compare [3] with 3
        //      then 3 with 3, and see that they are equal, which is what the problem wants
        //  for two integers, we just compare them
        //idea: Part II: sort using the compare function we wrote
        List<char[]> packets = new ArrayList<>();
        packets.add("[2]".toCharArray());
        packets.add("[6]".toCharArray());
        String[] arr = read_all_String();
        int ct = 0;
        for(int i = 0; i < arr.length; i += 3){
            int comp = compare(arr[i].toCharArray(), 0, arr[i].length() - 1, arr[i + 1].toCharArray(), 0, arr[i + 1].length() - 1);
            packets.add(arr[i].toCharArray());
            packets.add(arr[i + 1].toCharArray());
            //System.out.println("result: " + comp);
            ct += comp >= 0 ? (i / 3 + 1) : 0;
            //System.out.println("ct: " + ct);
        }
        Collections.sort(packets, (a, b) -> (compare(a, 0, a.length - 1, b, 0, b.length - 1) > 0 ? -1 : 1));
        int d1 = 0, d2 = 0;
        for(int i = 0; i < packets.size(); ++i){
            //print(packets.get(i));
            if(equal("[2]", packets.get(i))){
                d1 = i + 1;
            }
            else if(equal("[6]", packets.get(i))){
                d2 = i + 1;
            }
        }
        System.out.println("Task 1: " + ct);
        System.out.println("Task 2: " + (d1 * d2));
    }
    

    //template
    public static Scanner sc;
    static long MOD = 1000000007L;
    //(x + dir_card[i], y + dir_card[i + 1]) are 4 cardinal directions (for i in [0..3])
    public static int[] dir_card = {1, 0, -1, 0, 1};
    //same thing, 8 directions, for i in [0..7]
    public static int[] dir_all = {1, 0, -1, 0, 1, -1, -1, 1, 1};

    public static int[][] rotate_90(int[][] arr){
        int row = arr.length, col = arr[0].length;
        int[][] res = new int[col][row];
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                res[j][row - 1 - i] = arr[i][j];
            }
        }
        return res;
    }
    public static int[][] transpose(int[][] arr){
        int row = arr.length, col = arr[0].length;
        int[][] res = new int[col][row];
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                res[j][i] = arr[i][j];
            }
        }
        return res;
    }
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
    @SuppressWarnings("unchecked")
    public static <T> T[] subarray(T[] arr, int st, int end){
        assert(end > st);
        T[] res = (T[]) new Object[end - st];
        for(int i = st; i < end; ++i){
            res[i - st] = arr[i];
        }
        return res;
    }
    public static String[] subarray(String[] arr, int st, int end){
        assert(end > st);
        String[] res = new String[end - st];
        for(int i = st; i < end; ++i){
            String S = "";
            S += arr[i];
            res[i - st] = S;
        }
        return res;
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

    public static long[] stol(String[] toks){
        long[] arr = new long[toks.length];
        for(int i = 0; i < toks.length; ++i){
            arr[i] = Long.parseLong(toks[i]);
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
            for(int j = 0; j < aa.length; ++j)
                System.out.print(aa[i][j] + "\t");
            System.out.println();
        }
        System.out.println();
    }
    public static void print(int[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i] + "\t");
        System.out.println();
    }
    public static void print(long[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i] + "\t");
        System.out.println();
    }
    public static void print(char[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i]);
        System.out.println();
    }
    public static void print(int[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa[0].length; ++j)
                System.out.print(aa[i][j] + "\t");
            System.out.println();
        }
        System.out.println();
    }
    public static void print(int[][][] aa){
        for(int k = 0; k < aa[0][0].length; ++k){
            for(int i = 0; i < aa.length; ++i){
                for(int j = 0; j < aa[0].length; ++j){
                    System.out.print(aa[i][j][k] + "\t");
                }
                System.out.println();
            }
            System.out.println();
        }
        System.out.println();
    }
    public static void print(char[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa[0].length; ++j)
                System.out.print(aa[i][j]);
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