import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static class SegTree {
        long[] tree;
        int n;
        public SegTree(int[] nums) {
            if (nums.length > 0) {
                n = nums.length;
                tree = new long[n * 4];
                //buildTree(nums);
            }
        }
        private void buildTree(int[] nums) {
            for (int i = n, j = 0;  i < 2 * n; i++,  j++)
                tree[i] = (long)nums[j];
            for (int i = n - 1; i > 0; --i)
                tree[i] = tree[i * 2] + tree[i * 2 + 1];
        }


        void update(int pos, long val) {
            //System.out.println("update: " + pos + " " + val);
            pos += n;
            tree[pos] = val;
            while (pos > 0) {
                int left = pos;
                int right = pos;
                if (pos % 2 == 0) {
                    right = pos + 1;
                } else {
                    left = pos - 1;
                }
                // parent is updated after child is updated
                tree[pos / 2] = tree[left] + tree[right];
                pos /= 2;
            }
        }
        public long sumRange(int l, int r) {
            // get leaf with value 'l'
            l += n;
            // get leaf with value 'r'
            r += n;
            long sum = 0;
            while (l <= r) {
                if ((l % 2) == 1) {
                   sum += tree[l];
                   l++;
                }
                if ((r % 2) == 0) {
                   sum += tree[r];
                   r--;
                }
                l /= 2;
                r /= 2;
            }
            return sum;
        }
        
        public void print(){
            for(int i = 0; i < n * 4; ++i){
                System.out.print(tree[i]+",");
            }
            System.out.println();
        }
    }
    public static final long MAX = (long)(1e7);
    public static long convert(long a, long b){
        return a * MAX + b;
    }
    public static long manhattan(long x1, long y1, long x2, long y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        //idea: Simulate each y-coordinate one by one
        //Really ugly implementation with Segment Tree (just wanted to get warmed up again with Seg Tree)
        //  Noticed that for each y coordinate, for each current interval, only the 2 elements on the two boundaries of each interval
        //  will get removed or added, so we create a Segment Tree s.t. for each x-coordinate, if it's in some interval, its value in the tree
        //  is set to 1, otherwise 0. For each y-coordinate we can sum the elements in the Seg Tree subtracting number of beacons on the y-coordinate
        //  to get the answer for any y for task 1
        //  We keep an additional array count s.t. count[x] is how many intervals contain x, to know when to set a value in Seg Tree to be 0
        //      Say x is in 2 intervals, if it gets out of one interval, it's still in the other, but we couldn't keep that info in Seg Tree
        //      As Seg Tree[x] only has value 1 or 0. So we keep count to record how many intervals x are actually in.
        //      So that when it gets out of an interval, we do count[x]--, and check if count[x] == 0, and only then do we set Seg Tree[x] to 0
        //  Same thing for count, for each y-coordinate for each interval, only the values of 2 elements on the boundary needs to have their value changed in count
        String[] input_str = read_all_String();
        int N = input_str.length;
        long task1_y = 2000000, task2_y = 4000000;
        long[][] input = new long[N][4];
        //Record all events like when an interval first appears
        //when it changes from expanding along x to shrinking
        //and when it disappears
        HashMap<Long, HashSet<Triple<Integer, Integer, Integer>>> events = new HashMap<>();
        //intervals[i] stores info for the x range for the i-th scanner (intervals is updated every y-coordinate)
        //  [i][0] = x range's left bound, [i][1] = x range's right bound, [i][2] = 1 if x range expanding, = -1 if shrinking, 0 if no x range for scanner i at
        //  the current y-coordinate
        int[][] intervals = new int[N][3];

        //stores coordinate of beacons (y, list of x) for different beacons on each y
        HashMap<Long, HashSet<Long>> beacon = new HashMap<>();

        for(int i = 0; i < N; ++i){
            input[i] = stol(split(input_str[i], "[Senoisratclebxy= :,]"));
        }

        long min_y = Long.MAX_VALUE, max_y = Long.MIN_VALUE, min_x = Long.MAX_VALUE, max_x = Long.MIN_VALUE;
        for(int i = 0; i < N; ++i){
            long[] arr = input[i];
            long dist = manhattan(arr[0], arr[1], arr[2], arr[3]);
            min_y = Math.min(min_y, arr[1] - dist);
            max_y = Math.max(max_y, arr[1] + dist);
            min_x = Math.min(min_x, arr[0] - dist);
            max_x = Math.max(max_x, arr[0] + dist);
            beacon.putIfAbsent(arr[3], new HashSet<>());
            beacon.get(arr[3]).add(arr[2]);
            //fixes the bug where when intervals[i][0] == [i][1] == [i][2] = 0, intervals[i][0] reduces by 1 (we don't want this)
            //  so we initially set intervals[i][0] to be 1 != 0 = [i][1]
            intervals[i][0] = 1;
        }
        for(int i = 0; i < N; ++i){
            long[] arr = input[i];
            //shift x coordinate
            arr[0] -= min_x;
            arr[2] -= min_x;
            long dist = manhattan(arr[0], arr[1], arr[2], arr[3]);
            events.putIfAbsent(arr[1] - dist, new HashSet<>());
            events.putIfAbsent(arr[1] + 1, new HashSet<>());
            events.putIfAbsent(arr[1] + dist + 1, new HashSet<>());
            //event for interval appearing
            events.get(arr[1] - dist).add(new Triple<>((int)arr[0], i, 1));
            //event for interval going from expanding to shrinking
            events.get(arr[1] + 1).add(new Triple<>((int)arr[0], i, -1));
            //event for interval disappearing
            events.get(arr[1] + dist + 1).add(new Triple<>((int)arr[0], i, 0));
        }

        int x_span = (int)(max_x - min_x + 1);
        int[] count = new int[x_span];
        //answer for part 1
        long specific = 0L;
        SegTree seg = new SegTree(count);
        //answer for part 2
        long task2_ans_x = 0L, task2_ans_y = 0L;
        
        //simulate     
        for(long i = min_y; i <= max_y; ++i){
            if(events.containsKey(i)){
                for(Triple<Integer, Integer, Integer> t : events.get(i)){
                    intervals[t.b][2] = t.c;
                    //interval appearing, set left and right boundary to t.a
                    if(t.c == 1){
                        intervals[t.b][0] = t.a;
                        intervals[t.b][1] = t.a;
                    }
                    //interval starting to shrink
                    //reduce interval
                    //(in the previous iteration, the interval over-expanded by 1)
                    //we want to shrink it by 1 so that the interval is the same as the previous y-coordinate
                    //then we can iteratively remove the boundaries
                    //for instance, say the previous iteration, the interval range is [3, 7]
                    //in this shrinking iteration, we keep it [3, 7], but instead remove the boundaries
                    //so in fact after removing, it only has +1 in [4, 6] in "count".
                    else if(t.c == -1){
                        intervals[t.b][0]++;
                        intervals[t.b][1]--;
                    }
                }
            }
            for(int[] in : intervals){
                //print(in);
                if(in[2] == -1){
                    count[in[1]]--;
                    if(count[in[1]] == 0){
                        seg.update(in[1], 0);
                    }
                    //deals with the edge case when in[1] == in[1] (interval first appearing and when disappearing)
                    if(in[1] != in[0]){
                        count[in[0]]--;
                        if(count[in[0]] == 0){
                            seg.update(in[0], 0);
                        }
                    }
                }
                else if(in[2] == 1){
                    //System.out.println(count[in[1]] + " " + count[in[0]]);
                    count[in[1]]++;
                    if(count[in[1]] == 1){
                        seg.update(in[1], 1);
                    }
                    if(in[1] != in[0]){
                        count[in[0]]++;
                        if(count[in[0]] == 1){
                            seg.update(in[0], 1);
                        }
                    }
                }
                else if(in[0] == in[1]){
                    //removes the last element in the interval when interval disappears
                    count[in[0]]--;
                    if(count[in[0]] == 0){
                        seg.update(in[0], 0);
                    }

                    //to make in[0] > in[1], so that this interval never falls into this else if again
                    ++in[0];
                }
                //update interval according to in[2] (either -1, 0, or 1, -1 shrinks, 1 expands, 0 does nothing to the interval)
                in[0] -= in[2];
                in[1] += in[2];
            }
            //Part I -------------------
            long sum = seg.sumRange(0, x_span - 1);
            if(i == task1_y){
                specific = sum;
                if(beacon.containsKey(i)){
                    specific -= beacon.get(i).size();
                }
            }
            //Part I -------------------

            //Part II-------------------
            //sum range between 0 and 4000000, if sum < 4000001, means some x is not in any interval, then find such x
            long sum_distress = seg.sumRange((int)(-1 * min_x), (int)Math.min(-1 * min_x + task2_y, x_span - 1));
            if(i >= 0 && i <= task2_y && sum_distress < task2_y + 1){
                for(long j = -1 * min_x; j <= -1 * min_x + task2_y; ++j){
                    if(count[(int)j] == 0){
                        task2_ans_x = j + min_x;
                        task2_ans_y = i;
                    }
                }
            }
            //Part II-------------------
        }
        System.out.println("Task 1: " + specific);
        System.out.println("Task 2: " + task2_ans_x + " " + task2_ans_y + " " + (task2_ans_x * 4000000 + task2_ans_y));

    }
    

    //template
    public static Scanner sc;
    static long MOD = 1000000007L;
    //(x + dir_card[i], y + dir_card[i + 1]) are 4 cardinal directions (for i in [0..3])
    public static int[] dir_card = {1, 0, -1, 0, 1};
    //same thing, 8 directions, for i in [0..7]
    public static int[] dir_all = {1, 0, -1, 0, 1, -1, -1, 1, 1};

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