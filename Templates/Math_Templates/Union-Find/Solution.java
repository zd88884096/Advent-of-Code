class Solution{
    public static class Union{
        int[] p, sz;
        public Union(int n){
            p=new int[n];
            sz=new int[n];
            for(int i=0;i<n;++i){
                p[i]=i;
                sz[i]=1;
            }
        }
        public int find(int n){
            if(p[n]!=n){
                p[n]=find(p[n]);
            }
            return p[n];
        }
        public int union(int i, int j) {
            i=find(i);
            j=find(j);
            if(i!=j){        
                p[i]=j;
                sz[j]+=sz[i];
                return sz[i];
            }
            return -1;
        }
        public void print(){
            for(int i=0;i<p.length;++i){
                System.out.print(p[i]+" ");
            }
            System.out.println();
            for(int i=0;i<sz.length;++i){
                System.out.print(sz[i]+" ");
            }
            System.out.println();
        }
    }
}