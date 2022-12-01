import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static Scanner sc;
    public static void sol1(){
        try{
            //Solution 1: Sort List
            //n = # of elfs, m = # of stuff for each elf
            //Time Complexity: O(nmlog(n))
            File myObj = new File("input.txt");
            sc = new Scanner(myObj);
            //record values for each elf
            List<Integer> l = new ArrayList<>();
            //ct = current elf? value
            int ct = 0;
            while(sc.hasNextLine()){
                String S = read();
                if(S.length() == 0){
                    l.add(ct);
                    //reset ct for the next elf
                    ct = 0;
                }
                else{
                    ct += Integer.parseInt(S);
                }
            }
            //deal with the last elf
            l.add(ct);
            //sort decreasingly
            Collections.sort(l, (a, b) -> b - a);
            System.out.println("Solution 1:");
            System.out.println("Task 1: " + l.get(0));
            //very emmmmm....
            System.out.println("Task 2: " + (l.get(0) + l.get(1) + l.get(2)));
            System.out.println();
        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");        
        }
    }
    public static void sol2(){
        try{
            //Solution 2: PriorityQueue
            //Time Complexity: O(nm)
            sc = new Scanner(new File("input.txt"));
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
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");        
        }
    }
    public static void main(String[] args){
        sol1();
        sol2();
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