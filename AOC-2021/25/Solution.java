import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static Scanner sc;
    public static char[][] rotate(char[][] arr){
        int row = arr.length, col = arr[0].length;
        char[][] res = new char[col][row];
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                if(arr[i][j] == '>'){
                    res[j][i] = 'v';
                }
                else if(arr[i][j] == 'v'){
                    res[j][i] = '>';
                }
                else{
                    res[j][i] = arr[i][j];
                }
            }
        }
        return res;
    }
    public static int move(char[][] arr){
        int row = arr.length, col = arr[0].length;
        int ct = 0;
        for(int i = 0; i < row; ++i){
            int p1 = -1;
            for(int j = 0; j < col; ++j){
                if(arr[i][j] != '>'){
                    p1 = j;
                    break;
                }
            }
            if(p1 < 0){
                continue;
            }
            List<Integer> end = new ArrayList<>();
            for(int j = 0; j < col; ++j){
                int c = (p1 + j) % col, cn = (c + 1) % col;
                if(arr[i][c] == '>' && arr[i][cn] == '.'){
                    end.add(c);
                }
            }
            ct += end.size();
            for(int ele : end){
                int ne = (ele + 1) % col;
                arr[i][ne] = '>';
                arr[i][ele] = '.';
            }
        }
        return ct;
    }
    public static void main(String[] args){
        char[][] arr = list_to_char_arr(read_input());
        int ct = 0;
        while(true){
            int a = move(arr);
            arr = rotate(arr);
            int b = move(arr);
            arr = rotate(arr);
            ++ct;
            if(a + b == 0){
                break;
            }
        }
        System.out.println(ct);
    }

    //template
    static long MOD = 1000000007L;

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
    public static char[][] list_to_char_arr(List<String> l){
        char[][] arr = new char[l.size()][l.get(0).length()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = l.get(i).toCharArray();
        }
        return arr;
    }
    public static int[] list_to_int_arr(List<String> l){
        int[] arr = new int[l.size()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = Integer.parseInt(l.get(i));
        }
        return arr;
    }
    public static String[] list_to_String_arr(List<String> l){
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
    public static void print(int[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa[0].length; ++j){
                System.out.print(aa[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }
    public static void print(char[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa[0].length; ++j){
                System.out.print(aa[i][j]);
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