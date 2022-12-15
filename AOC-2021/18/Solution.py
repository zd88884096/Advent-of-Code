# understood the idea
# modified and adopted from https://github.com/womogenes/AoC-2021-Solutions/tree/main/day_18
with open("./input.txt") as fin:
    raw_data = fin.read().strip().split("\n")
data = [eval(line) for line in raw_data]

class Node:
    def __init__(self, val = None):
        self.val = val
        self.left = None
        self.right = None
        self.par = None

    def __str__(self):
        if isinstance(self.val, int):
            return str(self.val)
        return f"[{str(self.left)},{str(self.right)}]"

def parse(fish_num):
    root = Node()
    if isinstance(fish_num, int):
        root.val = fish_num
        return root

    root.left = parse(fish_num[0])
    root.right = parse(fish_num[1])
    root.left.par = root
    root.right.par = root

    reduce(root)
    return root

def add(a, b):
    root = Node()
    root.left = a
    root.right = b
    root.left.par = root
    root.right.par = root
    reduce(root)
    return root

def magnitude(root):
    if isinstance(root.val, int):
        return root.val
    return 3 * magnitude(root.left) + 2 * magnitude(root.right)

def reduce(root):
    done = True
    stack = [(root, 0)]
    while(len(stack) > 0):
        node, depth = stack.pop()
        if node == None:
            continue
        condition = node.left != None and node.right != None and (node.left.val != None and node.right.val != None)

        if depth >= 4 and node.val == None and condition:
            prev_node = node.left
            cur_node = node
            while cur_node != None and (cur_node.left == prev_node or cur_node.left == None):
                prev_node = cur_node
                cur_node = cur_node.par

            if cur_node != None:
                cur_node = cur_node.left
                while cur_node.val == None:
                    if cur_node.right != None:
                        cur_node = cur_node.right
                    else:
                        cur_node = cur_node.left
                cur_node.val += node.left.val

            prev_node = node.right
            cur_node = node
            while cur_node != None and (cur_node.right == prev_node or cur_node.right == None):
                prev_node = cur_node
                cur_node = cur_node.par

            if cur_node != None:
                cur_node = cur_node.right
                while cur_node.val == None:
                    if cur_node.left != None:
                        cur_node = cur_node.left
                    else:
                        cur_node = cur_node.right
                cur_node.val += node.right.val

            node.val = 0
            node.left = None
            node.right = None
            done = False
            break
        stack.append((node.right, depth + 1))
        stack.append((node.left, depth + 1))

    if not done:
        reduce(root)
        return

    stack = [root]
    while len(stack) > 0:
        node = stack.pop()
        if node == None:
            continue
        if node.val != None:
            assert node.left == None and node.right == None
            if node.val >= 10:
                node.left = Node(node.val // 2)
                node.right = Node(node.val - (node.val // 2))
                node.left.par = node
                node.right.par = node
                node.val = None

                done = False
                break
        stack.append(node.right)
        stack.append(node.left)
    if not done:
        reduce(root)

root = parse(data[0])

i = 1
while i < len(data):
    root = add(root, parse(data[i]))
    i += 1

ans = magnitude(root)
print(ans)

ma = 0
for i in range(len(data)):
    for j in range(len(data)):
        if i == j:
            continue
        ma = max(ma, magnitude(add(parse(data[i]), parse(data[j]))))
print(ma)