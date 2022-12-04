import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
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
    public static boolean equal(HashSet<Character> s1, HashSet<Character> s2){
        if(s1.size() != s2.size()){
            return false;
        }
        for(char ele : s1){
            if(!s2.contains(ele)){
                return false;
            }
        }
        return true;
    }
    public static HashSet<Character> inter(HashSet<Character> s1, HashSet<Character> s2){
        HashSet<Character> set = new HashSet<>();
        for(char ele : s1){
            if(s2.contains(ele)){
                set.add(ele);
            }
        }
        return set;
    }
    public static HashSet<Character> union(HashSet<Character> s1, HashSet<Character> s2){
        HashSet<Character> set = new HashSet<>();
        for(char ele : s1){
            set.add(ele);
        }
        for(char ele : s2){
            set.add(ele);
        }
        return set;
    }
    public static HashSet<Character> diff(HashSet<Character> s1, HashSet<Character> s2){
        HashSet<Character> set = new HashSet<>();
        for(char ele : s1){
            if(!s2.contains(ele)){
                set.add(ele);
            }
        }
        return set;
    }
    public static HashSet<Character> diff(HashSet<Character> s1, char c2){
        HashSet<Character> set = new HashSet<>();
        for(char ele : s1){
            if(ele != c2){
                set.add(ele);
            }
        }
        return set;
    }
    public static char first(HashSet<Character> s1){
        char c = '1';
        for(char ele : s1){
            c = ele;
            break;
        }
        return c;
    }
    public static void print(HashSet<Character> s1){
        for(char ele : s1){
            System.out.print(ele + "\t");
        }
        System.out.println();
    }
    public static void main(String[] args){
        String[] arr = read_all_String();
        HashSet<Character>[] digit_LED = new HashSet[10];
        char[] zero = {'a', 'b', 'c', 'e', 'f', 'g'};
        char[] one = {'c', 'f'};
        char[] two = {'a', 'c', 'd', 'e', 'g'};
        char[] three = {'a', 'c', 'd', 'f', 'g'};
        char[] four = {'b', 'c', 'd', 'f'};
        char[] five = {'a', 'b', 'd', 'f', 'g'};
        char[] six = {'a', 'b', 'd', 'e', 'f', 'g'};
        char[] seven = {'a', 'c', 'f'};
        char[] eight = {'a', 'b', 'c', 'd', 'e', 'f', 'g'};
        char[] nine = {'a', 'b', 'c', 'd', 'f', 'g'};
        List<char[]> l = new ArrayList<>();
        l.add(zero); l.add(one); l.add(two); l.add(three); l.add(four);
        l.add(five); l.add(six); l.add(seven); l.add(eight); l.add(nine);
        for(int i = 0; i < 10; ++i){
            digit_LED[i] = setify(l.get(i));
        }
        int score = 0;
        for(String S : arr){
            String[] toks = split(S, "[\\|]"), digits = split(toks[0], " "), code = split(toks[1], " ");
            Arrays.sort(digits, (a, b) -> a.length() - b.length());
            HashMap<Character, Character> map = new HashMap<>();
            for(char c = 'a'; c <= 'g'; ++c){
                map.put(c, '$');
            }
            HashSet<Character>[] digs = new HashSet[10], digs_comp = new HashSet[10];
            for(int i = 0; i < 10; ++i){
                digs[i] = setify(digits[i]);
            }
            for(int i = 0; i < 10; ++i){
                digs_comp[i] = diff(digs[9], digs[i]);
                //System.out.print(i + " ");
                //print(digs_comp[i]);
            }

            //1 and 7 deduces a
            char ca = first(diff(digs[1], digs[0]));
            map.put(ca, 'a');

            HashSet<Character> six_comp_union = union(digs_comp[6], union(digs_comp[7], digs_comp[8]));
            HashSet<Character> five_comp_union = union(digs_comp[3], union(digs_comp[4], digs_comp[5]));
            //d appears in digits with 6 lights but not those with 5 lights
            //print(six_comp_union);
            //print(five_comp_union);
            char cd = first(diff(six_comp_union, five_comp_union));
            map.put(cd, 'd');

            char cb = first(diff(diff(digs[2], digs[0]), cd));
            map.put(cb, 'b');

            char cf = 'x';
            for(int j = 3; j <= 5; ++j){
                if(!digs_comp[j].contains(cb)){
                    for(int k = 3; k <= 5; ++k){
                        if(inter(digs_comp[j], digs_comp[k]).size() == 0){
                            cf = first(diff(union(digs_comp[k], new HashSet<>()), cb));
                        }
                    }
                }
            }
            map.put(cf, 'f');

            char cc = first(diff(digs[0], cf));
            map.put(cc, 'c');

            char ce = 'x';
            for(int j = 6; j <= 8; ++j){
                if((!digs_comp[j].contains(cd)) && !digs_comp[j].contains(cc)){
                    ce = first(digs_comp[j]);
                    map.put(ce, 'e');
                }
            }

            char cg = 'x';
            for(char c : map.keySet()){
                if(map.get(c) == '$'){
                    cg = c;
                    map.put(c, 'g');
                    break;
                }
            }

            //System.out.println(ca + " " + cb + " " + cc + " " + cd + " " + ce + " " + cf + " " + cg);
            //System.out.println();
            int val = 0;
            loop: for(String C : code){
                val *= 10;
                HashSet<Character> sc = new HashSet<>();
                for(char c : C.toCharArray()){
                    sc.add(map.get(c));
                }
                for(int i = 0; i < 10; ++i){
                    if(equal(sc, digit_LED[i])){
                        val += i;
                        continue loop;
                    }
                }
            }
            score += val;
        }
        System.out.println(score);

        //part I
        int ct_special = 0;
        for(String S : arr){
            String[] toks = split(S, "[\\|]"), digits = split(toks[0], " "), code = split(toks[1], " ");
            Arrays.sort(digits, (a, b) -> a.length() - b.length());
            for(String c : code){
                if(c.length() <= 4 || c.length() >= 7){
                    ++ct_special;
                }
            }
        }
        System.out.println(ct_special);
    }

    //template
    public static Scanner sc;
    static long MOD = 1000000007L;

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
    //read all input into a String array
    public static String[] read_all_String(){
        List<String> l = read_input();
        String[] arr = new String[l.size()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = l.get(i);
        }
        return arr;
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
    public static void print(String[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.println(aa[i]);
        System.out.println();
    }
    public static void print_short(int[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i]);
        System.out.println();
    }
    public static void print(int[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa[0].length; ++j){
                System.out.print(aa[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }
    public static class Pair{
        int a;
        int b;
        public Pair(int ap, int bp){
            a = ap;
            b = bp;
        }
    }
    public static class Triple{
        int a;
        int b;
        int c;
        public Triple(int ap, int bp, int cp){
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