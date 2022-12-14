import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static Scanner sc;
    public static void main(String[] args){
        try{
            sc = new Scanner(new File("input.txt"));
        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");        
        }
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        int ct = 0;
        while(sc.hasNextLine()){
            String S = read();
            if(S.length() == 0){
                pq.add(ct);
                //limit pq's size to <= 3, add / poll takes O(log(3)) = O(1)
                //the removed element is the smallest in pq, which would
                //  never make top 3 anyway
                if(pq.size() > 3){
                    pq.poll();
                }
                ct = 0;
            }
            else{
                ct += Integer.parseInt(S);
            }
        }
        pq.add(ct);
        pq.poll();
        int a = pq.poll(), b = pq.poll(), c = pq.poll();
        System.out.println("Solution 2:");
        System.out.println("Task 1: " + c);
        //very emmmmm....
        System.out.println("Task 2: " + (a + b + c));
    }

    //template
    static long MOD = 1000000007L;
    public static int[] list_to_arr(List<Integer> l){
        int[] arr = new int[l.size()];
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