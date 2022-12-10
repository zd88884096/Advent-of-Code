import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {

    public static int[] cost = {1, 10, 100, 1000};
    //String that represents a state to a Stack<Integer>[] that does the same
    //Stack easier to change state, stack[i] stores all stuff on a peg from bottom to top
    @SuppressWarnings("unchecked")
    public static Stack<Integer>[] decode(String S){
        String[] toks = S.split(" ");
        Stack<Integer>[] res = new Stack[9];
        for(int i = 0; i < toks.length; ++i){
            res[i] = new Stack<>();
            for(int j = 0; j < toks[i].length(); ++j){
                res[i].add((int)(toks[i].charAt(j) - '0'));
            }
        }
        if(toks.length < 9){
            for(int i = toks.length; i < 9; ++i){
                res[i] = new Stack<>();
            }
        }
        for(Stack<Integer> stack : res){
            System.out.println(stack.size());
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    public static Stack<Integer>[] copy(Stack<Integer>[] stack){
        Stack<Integer>[] res = new Stack[stack.length];
        for(int i = 0; i < stack.length; ++i){
            Stack<Integer> l = stack[i];
            res[i] = new Stack<>();
            for(int j = 0; j < l.size(); ++j){
                res[i].add(l.get(j));
            }
        }
        return res;
    }

    //change stack[] representation of state to a String
    //String easier for Hashing
    public static String encode(Stack<Integer>[] stack){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < stack.length; ++i){
            for(int j = 0; j < stack[i].size(); ++j){
                sb.append(stack[i].get(j));
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    public static boolean empty(Stack<Integer> stack){
        for(int i = stack.size() - 1; i >= 0; --i){
            if(stack.get(i) != 9){
                return false;
            }
        }
        return true;
    }

    public static int top(Stack<Integer> stack){
        for(int i = stack.size() - 1; i >= 0; --i){
            if(stack.get(i) != 9){
                return stack.get(i);
            }
        }
        return -1;
    }

    public static int top_ind(Stack<Integer> stack){
        for(int i = stack.size() - 1; i >= 0; --i){
            if(stack.get(i) != 9){
                return i;
            }
        }
        return -1;
    }

    public static int top_empty(Stack<Integer> stack){
        for(int i = stack.size() - 1; i >= 0; --i){
            if(stack.get(i) == 9){
                return i;
            }
        }
        return -1;
    }

    //return cost to remove
    public static int remove(Stack<Integer> stack){
        int ind = top_ind(stack);
        stack.set(ind, 9);
        return stack.size() - ind;
    }

    //return cost to add
    public static int add(int type, Stack<Integer> stack){
        int empty_ind = top_empty(stack), tot = 0;
        for(int i = empty_ind; i < stack.size() - 1; ++i){
            stack.set(i, stack.get(i + 1));
            tot += cost[stack.get(i)];
        }
        stack.set(stack.size() - 1, type);
        tot += cost[type];
        return tot;
    }

    public static boolean reachable(int src, int dest, Stack<Integer>[] stack){
        if(src > dest){
            int tmp = src;
            src = dest;
            dest = tmp;
        }
        for(int i = src + 1; i < dest; ++i){
            if(i % 2 == 1 && top(stack[i]) != -1 && !empty(stack[i])){
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args){
        //peg_size: {2, 2, 1, 2, 1, 2, 1, 2, 2};
        //hallway_ind (among pegs in peg_size): {0, 2, 4, 6, 8};
        //room_ind: {1, 3, 5, 7};
        HashSet<Integer> hallway = new HashSet<>(), room = new HashSet<>();
        for(int i = 0; i < 9; ++i){
            if(i % 2 == 0)
                hallway.add(i);
            else
                room.add(i);
        }
        //starting vs. target
        //Task 1
        //int[] peg_size = {2, 2, 1, 2, 1, 2, 1, 2, 2};
        //String st = " 1 1 1 1 1 1 1 1";
        String st = "99 01 9 32 9 21 9 03 99";
        String end = "99 00 9 11 9 22 9 33 99";
        //Task 2
        /*int[] peg_size = {2, 2, 1, 2, 1, 2, 1, 2, 2};
        String st = " 01  32  21  03 ";
        String end = " 00  11  22  33 ";*/

        //INTUITION OF THE PROBLEM:
        //recognize each state of the game as a vertex in a directed graph
        //  each edge goes from vertex v to u if v can reach u in one step
        //  in one step, one can go from room to any reachable hallway
        //      and from hallway to its final destination if any stuff in
        //      final destination also belongs there (if A wants to enter peg 1, 
        //      everything in peg 1 has to also be A)
        //We want to find shortest path from st to end, and each edge has positive weight
        //  (always takes energy to make a move), so we can use Dijkstra on the graph
        //Note we don't have to create the whole graph to run Dijkstra, just have
        //  to explore possible moves along the way
        PriorityQueue<Pair<String, Long>> pq = new PriorityQueue<>((_f, _s) -> (_f.b - _s.b > 0 ? 1 : -1));
        HashMap<String, Long> visited = new HashMap<>();
        pq.add(new Pair<>(st, 0L));
        while(!pq.isEmpty()){
            Pair<String, Long> p = pq.poll();
            if(p.a.equals(end)){
                System.out.println(p.b);
                return ;
            }
            Stack<Integer>[] stack = decode(p.a);
            visited.put(st, p.b);
            for(int sr : room){
                for(int de : hallway){
                    if(reachable(src, dest, stack) && top_empty(stack[de]) != -1 && top(stack[sr]) != -1){
                        Stack<Integer>[] s2 = copy(stack);
                        Stack<Integer> src = s2[sr], dest = s2[de];
                        int src_top = top(src), src_top_ind = top_ind(src);
                        long nd = p.b + add(src_top, dest) + (dist(sr, de) + remove(src)) * cost[src_top];
                        String ns = encode(s2);
                        if(!visited.containsKey(ns)){
                            pq.add(new Pair<>(ns, nd));
                        }
                    }
                }
            }
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