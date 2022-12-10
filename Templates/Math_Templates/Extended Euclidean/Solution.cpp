#include <bits/stdc++.h>
using namespace std;
typedef long long ll;
ll extended_euclidean(ll a,ll b,ll &x,ll &y)
{
    if(!b) 
    {
        x=1;y=0;
        return a;
    }
    ll d=extended_euclidean(b,a%b,x,y);
    ll t=x;
    x=y;
    y=t-(a/b)*y;
    return d;
}

int main(){
    return 0;
}

int: 0x12345678
big: 0x12345678
small: 0x78563412
>>24

big: 0x100 12
small: 0x100 78

int x = 0x12345678
int* add = &x
add = 0x100
