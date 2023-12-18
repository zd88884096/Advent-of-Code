import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static HashMap<Character, Integer> counter(char[] a){
        HashMap<Character, Integer> map = new HashMap<>();
        for(char c : a){
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        return map;
    }

    public static boolean K5(HashMap<Character, Integer> a){
        for(char c : a.keySet()){
            if(a.get(c) == 5){
                return true;
            }
        }
        return false;
    }

    public static boolean K4(HashMap<Character, Integer> a){
        for(char c : a.keySet()){
            if(a.get(c) == 4){
                return true;
            }
        }
        return false;
    }

    public static boolean FH(HashMap<Character, Integer> a){
        int san = 0, er = 0;
        for(char c : a.keySet()){
            if(a.get(c) == 3){
                ++san;
            }
            else if(a.get(c) == 2){
                ++er;
            }
        }
        return san > 0 && er > 0;
    }

    public static boolean K3(HashMap<Character, Integer> a){
        for(char c : a.keySet()){
            if(a.get(c) == 3){
                return true;
            }
        }
        return false;
    }

    public static boolean P2(HashMap<Character, Integer> a){
        int ct = 0;
        for(char c : a.keySet()){
            if(a.get(c) == 2){
                ++ct;
            }
        }
        return ct == 2;
    }

    public static boolean P1(HashMap<Character, Integer> a){
        int ct = 0;
        for(char c : a.keySet()){
            if(a.get(c) == 2){
                ++ct;
            }
        }
        return ct == 1;
    }

    public static boolean HC(HashMap<Character, Integer> a){
        return a.size() == 5;
    }

    public static int rank(HashMap<Character, Integer> a){
        if(K5(a)){
            return 10;
        }
        if(K4(a)){
            return 9;
        }
        if(FH(a)){
            return 8;
        }
        if(K3(a)){
            return 7;
        }
        if(P2(a)){
            return 6;
        }
        if(P1(a)){
            return 5;
        }
        if(HC(a)){
            return 4;
        }
        return 3;
    }
    public static char[] arr = {'J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A'};
    public static int value(char c){
        for(int i = 0; i < arr.length; ++i){
            if(c == arr[i]){
                return i;
            }
        }
        return -1;
    }
    public static int compare3(char[] a, char[] b){
        for(int i = 0; i < 5; ++i){
            if(a[i] != b[i]){
                return value(a[i]) - value(b[i]);
            }
        }
        return 0;
    }
    public static int compare2(char[] a, char[] b){
        HashMap ma = counter(a), mb = counter(b);
        int ra = rank(ma), rb = rank(mb);
        return ra - rb;
    }

    public static char[] copy(char[] a){
        char[] cp = new char[5];
        for(int i = 0; i < 5; ++i){
            cp[i] = a[i];
        }
        return cp;
    }
    public static List<char[]> la, lb;
    public static void maxify(char[] a, boolean aq){
        int ct = 0;
        for(int i = 0; i < 5; ++i){
            if(a[i] == 'J'){
                ++ct;
                for(int j = 1; j < arr.length; ++j){
                    a[i] = arr[j];
                    maxify(a, aq);
                    a[i] = 'J';
                }
            }
        }
        if(ct == 0){
            if(aq)
                la.add(copy(a));
            else
                lb.add(copy(a));
        }
    }

    public static char[] maxmax(char[] a){
        int cc = 0;
        for(char c : a){
            if(c == 'J'){
                ++cc;
            }
        }
        if(cc == 5){
            return "AAAAA".toCharArray();
        }
        if(cc == 4){
            char[] ac = copy(a);
            for(int i = 0; i < 5; ++i){
                if(ac[i] != 'J'){
                    for(int j = 0; j < 5; ++j){
                        ac[j] = ac[i];
                    }
                    return ac;
                }
            }
            return ac;
        }
        la = new ArrayList<>();
        maxify(copy(a), true);
        Collections.sort(la, (f, s) -> compare2(f, s));
        return la.get(la.size() - 1);
    }
    public static int compare(char[] a, char[] b){
        char[] ma = maxmax(a), mb = maxmax(b);
        int mc = compare2(ma, mb);
        if(mc != 0){
            return mc;
        }
        return compare3(a, b);
    }
    public static class PairP{
        char[] card;
        long bid;
        public PairP(char[] a, long b){
            card = a;
            bid = b;
        }
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        long min = Long.MAX_VALUE;
        List<List<long[]>> l = new ArrayList<>();
        String[] SS = read_all_String();
        int n = SS.length;
        List<PairP> cards = new ArrayList<>(n);
        for(int i = 0; i < n; ++i){
            String[] toks = split(SS[i], " ");
            System.out.println(i);
            cards.add(new PairP(toks[0].toCharArray(), Long.parseLong(toks[1])));
        }
        Collections.sort(cards, (a, b) -> compare(a.card, b.card));
        long res = 0L;
        for(long i = 0L; i < (long)n; ++i){
            print(cards.get((int)i).card);
            //System.out.println(cards.get((int)i).bid);
            res += (i + 1) * cards.get((int)i).bid;
        }
        System.out.println(res);
        //print(maxmax("88J8J".toCharArray()));
        //print(maxmax("JJ8JJ".toCharArray()));
        //System.out.println(compare("88J8J".toCharArray(), "JJ8JJ".toCharArray()));
    }
    public static String FILENAME = "input.txt";
    //template
    public static Scanner sc;
    //(x + dir_card[i], y + dir_card[i + 1]) are 4 cardinal directions (for i in [0..3])
    public static int[] dir_card = {1, 0, -1, 0, 1};
    //same thing, 8 directions, for i in [0..7]
    public static int[] dir_all = {1, 0, -1, 0, 1, -1, -1, 1, 1};

    public static String[] trim(String[] toks){
        for(int i = 0; i < toks.length; ++i){
            toks[i] = trim(toks[i]);
        }
        return toks;
    }
    public static String trim(String S){
        char[] s = trim(S.toCharArray());
        StringBuilder sb = new StringBuilder();
        for(char c : s){
            sb.append(c);
        }
        return sb.toString();
    }

    public static char[] trim(char[] s){
        int st = 0, end = s.length - 1;
        while(st < s.length){
            if(s[st] != ' '){
                break;
            }
            ++st;
        }
        while(end >= 0){
            if(s[end] != ' '){
                break;
            }
            --end;
        }
        if(st > end){
            char[] res = {};
            return res;
        }
        char[] res = new char[end - st + 1];
        for(int i = 0; i < end - st + 1; ++i){
            res[i] = s[i + st];
        }
        return res;
    }
    public static long MAX = (long)(1e6 + 0.1), MOD = 1000000007L,  _timer_ind = 0L;
    public static long[] _timer = new long[2];
    public static void time(){
        _timer[(int)_timer_ind] = System.nanoTime();
        _timer_ind = _timer_ind * -1 + 1;
    }

    //print timer result in the form of a formmated String
    public static void print_time(){
        System.out.println("Time: " + String.format("%.3f", (double)(_timer[1] - _timer[0]) / (double)(1e9)) + " sec");
    }

    //pad character c onto String S until it reaches target_len
    //pad on left if side == 0, right otherwise
    public static String pad(String S, char c, int target_len, int side){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < target_len - S.length(); ++i){
            sb.append(c);
        }
        return side == 0 ? (sb.toString() + S) : (S + sb.toString());
    }

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
    public static HashSet<Integer> setify(int[] a){
        HashSet<Integer> set = new HashSet<>();
        for(int ele : a){
            set.add(ele);
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
            sc = new Scanner(new File(FILENAME));
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