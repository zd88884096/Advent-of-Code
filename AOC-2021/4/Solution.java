import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static HashSet<Character> setify(String S){
        HashSet<Character> set = new HashSet<>();
        char[] a = S.toCharArray();
        for(int i = 0; i < a.length; ++i){
            set.add(a[i]);
        }
        return set;
    }
    public static void main(String[] args){
        String[] arr = read_all_String();
        List<int[][]> boards = new ArrayList<>();
        int ind = 2;
        while(ind < arr.length){
            int[][] board = new int[7][5];
            for(int i = 0; i < 5; ++i){
                int ind2 = 0;
                String[] toks = arr[i + ind].split(" ");
                for(int j = 0; j < 5; ++j){
                    while(toks[ind2].equals("")){
                        ++ind2;
                    }
                    board[i][j] = Integer.parseInt(toks[ind2]);
                    ++ind2;
                }
            }
            //board[5][i] = how many values are not marked in row i
            Arrays.fill(board[5], 5);
            //board[6][j] = how many values are not marked in col j
            Arrays.fill(board[6], 5);
            ind += 6;
            boards.add(board);
            //print(board);
        }
        String[] val = arr[0].split(",");
        int res = 0;
        //set completed_boards[i] to true if board i is completed
        boolean[] completed_boards = new boolean[boards.size()];
        loop: for(String S : val){
            int cur = Integer.parseInt(S);
            for(int e = 0; e < boards.size(); ++e){
                int[][] board = boards.get(e);
                //if marked true, compute the score and return
                boolean board_complete = false;
                loop2: for(int i = 0; i < 5; ++i){
                    for(int j = 0; j < 5; ++j){
                        if(board[i][j] == cur){
                            //so that next time if cur appears again in val, we don't mark it again
                            board[i][j] *= -1;
                            //System.out.println(cur + " " + i + " " + j);
                            board[5][i]--;
                            board[6][j]--;
                            if(board[5][i] == 0 || board[6][j] == 0){
                                board_complete = true;
                                break loop2;
                            }
                        }
                    }
                }
                // when boards[e] is completed, record its score
                if(board_complete && !completed_boards[e]){
                    completed_boards[e] = true;
                    int sum = 0;
                    for(int i = 0; i < 5; ++i){
                        for(int j = 0; j < 5; ++j){
                            //add all unmarked values
                            if(board[i][j] >= 0){
                                sum += board[i][j];
                            }
                        }
                    }
                    //part 1
                    /*
                    System.out.println(sum + " " + cur + " " + (sum * cur));
                    return ;
                    */

                    //part 2, just store the score of each board when they finish, and write over "res", 
                    //  so in the end "res" will store score of the last finished board
                    //System.out.println(res);
                    res = sum * cur;
                }
            }
        }
        //part 2
        System.out.println(res);
    }

    //template
    public static Scanner sc;
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
    public static char[][] read_all_char_arr(){
        List<String> l = read_input();
        char[][] arr = new char[l.size()][l.get(0).length()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = l.get(i).toCharArray();
        }
        return arr;
    }
    public static int[] read_all_int(){
        List<String> l = read_input();
        int[] arr = new int[l.size()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = Integer.parseInt(l.get(i));
        }
        return arr;
    }
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