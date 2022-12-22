import re
import functools
import itertools
import collections
from collections import defaultdict
from collections import Counter
import sys
inf = sys.argv[1] if len(sys.argv) > 1 else 'input.txt'

ll = [x for x in open(inf).read().rstrip().split('\n')]

#board = [list(x.replace("#", ".")) for x in ll[:-2]]
board = [list(x) for x in ll[:-2]]
moves = ll[-1]
#print(len(ll), len(board))
sz = len(board)
maxlen = max([len(x) for x in board])
for l in board:
  while len(l) < maxlen:
    l.append(" ")
#print(board)
def inr(pos, arr):
  return pos[0] in range(len(arr)) and pos[1] in range(len(arr[0]))
def addt(x, y):
  if len(x) == 2:
    return (x[0] + y[0], x[1] + y[1])
  return tuple(map(sum, zip(x, y)))
pos = (0, board[0].index("."))
DIRS = [(0, 1), (1, 0), (0, -1), (-1, 0)]
CHRS = [">", "v", "<", "^"]
dr = (0, 1)

moves = moves.replace("L", " L ").replace("R", " R ")

copy = [list(tuple(x)) for x in board]

csl = len("#...#...#..#.#.....#.#....#....................#..") # cube side length
def addwrap(pos,dr):
  x = addt(pos, dr)
  if inr(x, board) and board[x[0]][x[1]] != " ":
    return (x, dr, False) # done
  if dr == (0, 1):
    # right
    if x[0] in range(0, 50):
      return ((49 - x[0] + 100, 99), (0, -1), True)
    if x[0] in range(50, 100):
      return ((49, x[0] - 50 + 100), (-1, 0), True)
    if x[0] in range(100, 150):
      return ((149 - x[0], 149), (0, -1), True)
    if x[0] in range(150, 200):
      return ((149, x[0] - 150 + 50), (-1, 0), True)
  if dr == (0, -1):
    # left
    if x[0] in range(0, 50):
      return ((49 - x[0] + 100, 0), (0, 1), True)
    if x[0] in range(50, 100):
      return ((100, x[0] - 50), (1, 0), True)
    if x[0] in range(100, 150):
      return ((149 - x[0], 50), (0, 1), True)
    if x[0] in range(150, 200):
      return ((0, x[0] - 150 + 50), (1, 0), True)
  if dr == (1, 0):
    # down
    if x[1] in range(0, 50):
      return ((0, x[1] + 100), (1, 0), True)
    if x[1] in range(50, 100):
      return ((150 + x[1] - 50, 49), (0, -1), True)
    if x[1] in range(100, 150):
      return ((50 + x[1] - 100, 99), (0, -1), True)
  if dr == (-1, 0):
    # up
    if x[1] in range(0, 50):
      return ((x[1] + 50, 50), (0, 1), True)
    if x[1] in range(50, 100):
      return ((150 + x[1] - 50, 0), (0, 1), True)
    if x[1] in range(100, 150):
      return ((199, x[1] - 100), (-1, 0), True)
copy[pos[0]][pos[1]] = CHRS[DIRS.index(dr)]
print(moves);
for move in moves.split(" "):
  if move == "R":
    dr = DIRS[(DIRS.index(dr) + 1) % 4]
    copy[pos[0]][pos[1]] = CHRS[DIRS.index(dr)]
  elif move == "L":
    dr = DIRS[(DIRS.index(dr) + 3) % 4]
    copy[pos[0]][pos[1]] = CHRS[DIRS.index(dr)]
  else:
    dist = int(move)
    for i in range(dist):
      nxt, maybedr, changed = addwrap(pos, dr)
      #while board[nxt[0]][nxt[1]] == " ":
      # nxt = addwrap(nxt, dr)
      if board[nxt[0]][nxt[1]] == ".":
      #if True:
        #if(changed):
        print(pos, dr, nxt, maybedr)
        pos = nxt
        dr = maybedr
        copy[pos[0]][pos[1]] = CHRS[DIRS.index(dr)]

'''
for line in copy:
  s = ""
  for ch in line:
    s += str(ch)
  print(s)
print()
'''

print(1000 * (pos[0]+1) + 4 * (pos[1] + 1) + DIRS.index(dr))