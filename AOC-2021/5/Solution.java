import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static void main(String[] args){
        String[] arr = read_all_String();
        int[][] lines = new int[arr.length][4];
        int max = 0;
        for(int s = 0; s < arr.length; ++s){
            String[] toks = split(arr[s], "[ \\-,\\>]");
            //print(toks);
            for(int i = 0; i < 4; ++i){
                lines[s][i] = Integer.parseInt(toks[i]);
                max = Math.max(max, lines[s][i]);
            }
        }
        int[][] count = new int[max + 1][max + 1];
        for(int[] l : lines){
            //part 1
            
            /*if(l[0] != l[2] && l[1] != l[3]){
                continue;
            }*/


            int x = l[0], y = l[1];
            //print(l);
            while(x != l[2] || y != l[3]){
                //System.out.println(x + " " + y);
                ++count[x][y];
                if(l[0] > l[2]){
                    --x;
                }
                else if(l[0] < l[2]){
                    ++x;
                }
                if(l[1] > l[3]){
                    --y;
                }
                else if(l[1] < l[3]){
                    ++y;
                }
            }
            //endpoint of a line
            ++count[l[2]][l[3]];
        }
        //print(count);
        int res = 0;
        for(int i = 0; i <= max; ++i){
            for(int j = 0; j <= max; ++j){
                if(count[i][j] > 1){
                    ++res;
                }
            }
        }
        System.out.println(res);
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