import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    //turns out I wrote a over-generalized version of the problem...
    //The input has only one monkey depending on the value of humn, so the final equation to evaluate for "root" is linear
    //  (since there are no cases where a * b is performed where a and b are both non-constant equations of x, otherwise some child of a and some child of
    //      b both depends on x)...
    //But the problem statement didn't say, oh well...

    //PART I: Traditional Topological Sort Algorithm, there are plenty of good materials to learn from online.

    //PART II:
    //I ended up using Pair<long[], long[]> to represent the value for each monkey, one long[] for numerator one for denomenator, each representing a polynomial
    //  (denominator is useful if performing a / b where b depends on x), if say numerator is {2, 3, 1}, it represents the equation 2x^2 + 3x + 1.

    //I wrote helper functions to multiply, subtract, and add polynomials, which is enough to perform all functionalities.
    //Each value is in the form of a / b. So for two values a / b and c / d, for any operations we perform on the two values, we have:
    //a / b + c / d = (ad + bc) / bd (equivalent to set the result's numerator to ad + bc, denominator to bd)
    //a / b - c / d = (ad - bc) / bd
    //a / b * c / d = ac / bd
    //(a / b) / (c / d) = ad / bc
    
    //I also had to divide each newly created value (Pair<long[], long[]>) by the GCD of all their coefficients to prevent long overflow.
    //  (For example, I reduced {3, 3, 6}, {3, 3} ((3x^2 + 3x + 6) / (3x + 3)) to {1, 1, 2}, {1, 1}, since 3 is the gcd of all coefficients)
    //  (Note we don't need this process again for the 2 operands used when computing a new value, since when those 2 were created, they already went
    //      through this process once)

    //I just computed the values for Part II in the same way as Part I, but now doing operations on Pair<long[], long[]>. In the end, at root we end up
    //  with equations a / b = c / d, this is equivalent to ad - bc = 0. The solution to this equation must be a divisor of the constant term of the equation.
    
    //So to get all divisors of a constant c, we just need to try all numbers from 1 to sqrt(c), since for any number d, a divisor for c, c / d is a divisor
    //  too, either d or c / d <= sqrt(c), so we just have to iterate from 1 to sqrt(c) and take any divisor of c and c / this number to get all divisors of c.
    
    //This takes at most sqrt(1e19) iterations around 3e9, which takes at most around 10 seconds to run on Java, and # of divisors is significantly smaller
    //  than 3e9.
    
    //For each divisor, evaluate the equation to see if the result is 0, if it is, it's the answer we want.


    //each entry is (monkey name, value in the form of equation of x with polynomial numerator and denominator)
    static HashMap<String, Pair<long[], long[]>> res;

    //each entry is (monkey name, Triple<operand 1 monkey name, operator, operand 2 monkey name>) specifying operation for each math operation monkey
    static HashMap<String, Triple<String, String, String>> prev;

    //each entry is (monkey m's name, HashSet<monkeys that depend on m's value>)
    static HashMap<String, HashSet<String>> forw;

    //multiplying polynomials
    public static long[] mult(long[] l1, long[] l2){
        long[] res = new long[l1.length + l2.length - 1];
        for(int i = 0; i < l1.length; ++i){
            for(int j = 0; j < l2.length; ++j){
                int pos = i + j;
                res[pos] += l1[i] * l2[j];
            }
        }
        return res;
    }

    //adding polynomials
    public static long[] add(long[] l1, long[] l2){
        long[] res = new long[Math.max(l1.length, l2.length)];
        int ind = 0;
        while(ind < l1.length && ind < l2.length){
            res[res.length - ind - 1] = l1[l1.length - ind - 1] + l2[l2.length - ind - 1];
            ++ind;
        }
        while(ind < l1.length){
            res[res.length - ind - 1] = l1[l1.length - ind - 1];
            ++ind;
        }
        while(ind < l2.length){
            res[res.length - ind - 1] = l2[l2.length - ind - 1];
            ++ind;
        }
        return res;
    }

    //subtracting polynomials (l1 - l2)
    public static long[] sub(long[] l1, long[] l2){
        long[] res = new long[Math.max(l1.length, l2.length)];
        int ind = 0;
        //System.out.println(res.length + " " + l1.length + " " + l2.length + " " + (l1.length - ind - 1));
        while(ind < l1.length && ind < l2.length){
            res[res.length - ind - 1] = l1[l1.length - ind - 1] - l2[l2.length - ind - 1];
            ++ind;
        }
        while(ind < l1.length){
            res[res.length - ind - 1] = l1[l1.length - ind - 1];
            ++ind;
        }
        while(ind < l2.length){
            res[res.length - ind - 1] = -1 * l2[l2.length - ind - 1];
            ++ind;
        }
        return res;
    }

    //evaluating polynomial l with unknown set to x
    public static long eval(long[] l, long x){
        long ans = 0L, m = 1L;
        for(int i = l.length - 1; i >= 0; --i){
            ans += m * l[i];
            m *= x;
        }
        return ans;
    }

    //compute gcd of a and b
    public static long gcd(long a, long b){
        return b == 0 ? a : gcd(b, a % b);
    }

    //compute gcd of all terms (coefficients) in arr
    public static long gcd_all(long[] arr){
        long ans = Math.abs(arr[0]);
        for(int i = 1; i < arr.length; ++i){
            ans = gcd(ans, Math.abs(arr[i]));
        }
        return ans;
    }

    //divide all coefficients in numerator and denominator by the gcd of all their coefficients
    public static void gcd_process(Pair<long[], long[]> p){
        long[] l1 = p.a, l2 = p.b;
        long g1 = gcd_all(l1), g2 = gcd_all(l2), g = gcd(g1, g2);
        for(int i = 0; i < l1.length; ++i){
            l1[i] /= g;
        }
        for(int j = 0; j < l2.length; ++j){
            l2[j] /= g;
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        //To Switch to Part I, just set this boolean to true
        boolean part1 = false;

        res = new HashMap<>();
        prev = new HashMap<>();
        forw = new HashMap<>();

        //topo sort setup
        for(String S : read_all_String()){
            String[] toks = split(S, "[ :]");
            forw.putIfAbsent(toks[0], new HashSet<>());
            if(toks.length == 2){
                long[] denom = {1L};
                long[] num = {};
                //value set to x / 1 (= x)
                if(toks[0].equals("humn") && !part1){
                    num = new long[2];
                    num[0] = 1L;
                    num[1] = 0L;
                }
                //value set to (long)(toks[1]) / 1 (= (long)(toks[1]), equivalent to numerical value)
                else{
                    num = new long[1];
                    num[0] = Long.parseLong(toks[1]);
                }
                res.put(toks[0], new Pair<>(num, denom));
            }
            else{
                String op1 = toks[1], op2 = toks[3], op = part1 ? toks[2] : (toks[0].equals("root") ? "=" : toks[2]);
                forw.putIfAbsent(op1, new HashSet<>());
                forw.putIfAbsent(op2, new HashSet<>());
                prev.put(toks[0], new Triple<>(op1, op, op2));
                forw.get(op1).add(toks[0]);
                forw.get(op2).add(toks[0]);
            }
        }
        Queue<String> q = new LinkedList<>();
        for(String ele : res.keySet()){
            q.add(ele);
        }
        long[] _dummy1 = {}, _dummy2 = {};

        //topo sort evaluation stage
        while(!q.isEmpty()){
            String cur = q.poll();
            for(String next : forw.get(cur)){
                Triple<String, String, String> trip = prev.get(next);
                if(res.containsKey(trip.a) && res.containsKey(trip.c)){
                    //breaks when we encounter "root" (means both operands for "root" have been computed)
                    //This only happens when "part1" is set to false.
                    if(trip.b.equals("=")){
                        break;
                    }
                    q.add(next);
                    Pair<long[], long[]> op1 = res.get(trip.a), op2 = res.get(trip.c), r = new Pair<>(_dummy1, _dummy2);

                    //a / b - c / d = (ad - bc) / bd
                    if(trip.b.equals("-")){
                        long[] bd = mult(op1.b, op2.b), ad = mult(op1.a, op2.b), bc = mult(op1.b, op2.a);
                        r.a = sub(ad, bc);
                        r.b = bd;
                    }
                    //a / b * c / d = ac / bd
                    else if(trip.b.equals("*")){
                        long[] ac = mult(op1.a, op2.a), bd = mult(op1.b, op2.b);
                        r.a = ac;
                        r.b = bd;
                    }
                    //a / b + c / d = (ad + bc) / bd
                    else if(trip.b.equals("+")){
                        long[] bd = mult(op1.b, op2.b), ad = mult(op1.a, op2.b), bc = mult(op1.b, op2.a);
                        r.a = add(ad, bc);
                        r.b = bd;
                    }
                    //(a / b) / (c / d) = ad / bc
                    else{
                        long[] ad = mult(op1.a, op2.b), bc = mult(op1.b, op2.a);
                        r.a = ad;
                        r.b = bc;
                    }

                    //divide all coefficients by their gcd to prevent long overflow
                    gcd_process(r);
                    res.put(next, r);
                }
            }
        }

        /*for(String S : res.keySet()){
            System.out.println(S);
            print(res.get(S).a);
            print(res.get(S).b);
        }*/

        Pair<long[], long[]> op1 = res.get(prev.get("root").a), op2 = res.get(prev.get("root").c);

        //equation := ad - bc, want to find an x that makes equation 0
        long[] ad = mult(op1.a, op2.b), bc = mult(op1.b, op2.a), equation = sub(ad, bc);
        //print(equation);
        long ans = -1L;

        //iterating through 1 and sqrt(constant term of equation), 1e-1 is EPS (for preventing the sqrt value to be 4.999999 and casted to 4L instead of 5L)
        loop: for(long div = 1L; div <= (long)Math.sqrt(Math.abs(equation[equation.length - 1]) + 1e-1); ++div){
            //div not a divisor for the constant term of equation
            if(equation[equation.length - 1] % div != 0){
                continue;
            }
            //all divisors associated with div, in this way, all divisors d of the constant term is guaranteed to be reached (if d > sqrt(constant), 
            //  constant / d < sqrt(constant), so d will be reached in the form of equation[equation.length - 1] / div, where div = constant / d)
            long[] values = {div, -1 * div, equation[equation.length - 1] / div, -1 * equation[equation.length - 1] / div};
            for(long ele : values){
                long e = eval(equation, ele);
                //System.out.println(ele + " res: " + e);
                if(e == 0L){
                    ans = ele;
                    break loop;
                }
            }
        }
        if(part1){
            System.out.println("Task 1:" + res.get("root").a[0]);
        }
        else{
            System.out.println("Task 2:" + ans);
        }
        //System.out.println("Task 1: " + res.get("root"));
    }

    //template
    public static Scanner sc;
    //(x + dir_card[i], y + dir_card[i + 1]) are 4 cardinal directions (for i in [0..3])
    public static int[] dir_card = {1, 0, -1, 0, 1};
    //same thing, 8 directions, for i in [0..7]
    public static int[] dir_all = {1, 0, -1, 0, 1, -1, -1, 1, 1};

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