import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static final long MAX = (long)(1e7), LIM = (long)(1e4);
    public static long convert(long a, long b){
        return a * MAX + b;
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        //idea: Simulate, add all rocks and all resting sand along the way to a HashSet "occupied"
        //  Simulate falling sand by using a Stack representing the path of falling sand from (500, 0) to its resting place,
        //      each element in the Stack is a coordinate that the sand passes through during its way of falling
        //
        //  In each iteration, the stack stores the path P1 of some sand S1, we want to compute the path P2 of the next sand S2
        //      and store P2 in stack efficiently
        //
        //  For the new sand, pop from stack's top (P1[-1]) (which is the previous sand's resting coordinate)
        //  Then simulate S2 falling starting from the new top of stack, adding coordinates that S2 passes through to the stack
        //      until it reaches a rest, then add the resting coordinate of S2 to "occupied"
        //
        //  This essentially avoids iteration through all the overlap coordinates between P1 and P2, which is P1[0..-2] (surprisingly P2 follow P1
        //      except S1's resting coordinate)
        //  
        //  We can prove the correctness of this algorithm by noting that:
        //  In each iteration, only the top of the stack (The resting coordinate of S1 will be occupied, 
        //  the rest of the path = P1[0..-2] will be unoccupied, so we claim S2 will follow P1[0..-2] without changes (i.e. the first coordinates that S2
        //      passes through will be P1[0..-2])
        //
        //  Pf for the claim:
        //      Prove with induction on P1[i], WTS P1[i] == P2[i] for all i in 0..(len(P1) - 2):
        //      Base case: P1[0] = (500, 0), both S1 and S2 start from (500, 0), so P1[0] = P2[0]
        //      Inductive case: Assume P1[k - 1] == P2[k - 1]
        //          If S1 fell down directly from P1[k - 1] to P1[k], S2 will too, since P1[k] is not occupied (otherwise S1 will have stopped at P1[k] != P1[-1],
        //              Since only the end of P1 is occupied after S1 rests)
        //          Same for the case when S1 fell left down or right down.

        int ct = 0;
        //view rocks and occupied sand in the same way: as rocks
        HashSet<Long> occupied = new HashSet<>();
        Stack<Long> path = new Stack<>();
        path.add(convert(500L, 0L));
        long max_y = 0L;

        //adding rocks to "occupied"
        for(String S : read_all_String()){
            long[] arr = stol(split(S, "[> ,-]"));
            for(int i = 0; i < arr.length - 3; i += 2){
                long i1 = arr[i], i2 = arr[i + 2], j1 = arr[i + 1], j2 = arr[i + 3];
                while(i1 != i2 || j1 != j2){
                    occupied.add(convert(i1, j1));
                    i1 += (i2 - i1) / (Math.abs(i2 - i1) == 0L ? 1 : Math.abs(i2 - i1));
                    j1 += (j2 - j1) / (Math.abs(j2 - j1) == 0L ? 1 : Math.abs(j2 - j1));
                }
                occupied.add(convert(i2, j2));
                max_y = Math.max(max_y, Math.max(j1, j2));
            }
        }
        //if you want to only see result from part I, just comment the part between
        //  Part II - START and Part II - END
        //Part II - START
        //adding floor, observe that the leftmost the sand can get is 500 - floor_y, 10 is for safety
        long floor_y = max_y + 2;
        for(long i = 500L - floor_y - 10; i <= 510L + floor_y + 10; ++i){
            occupied.add(convert(i, floor_y));
        }
        //Part II - END

        //simulate sand falling
        loop: while(true){
            if((!path.isEmpty()) && occupied.contains(path.get(path.size() - 1))){
                path.pop();
            }
            if(path.isEmpty()){
                break;
            }
            long cur = path.get(path.size() - 1);
            path.pop();
            while(true){
                //try to go down one step
                path.add(cur);

                //too deep, meaning sand reaches abyss
                if(cur % MAX >= LIM){
                    break loop;
                }
                //can go down
                if(!occupied.contains(cur + 1)){
                    cur += 1;
                }
                //can go left down
                else if(!occupied.contains(cur - MAX + 1)){
                    cur = cur - MAX + 1;
                }
                //can go right down
                else if(!occupied.contains(cur + MAX + 1)){
                    cur = cur + MAX + 1;
                }
                //reaches a rest
                else{
                    break;
                }
            }
            occupied.add(cur);
            ++ct;
        }
        System.out.println("Task 2: " + ct);
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