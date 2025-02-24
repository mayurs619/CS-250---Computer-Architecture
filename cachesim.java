import java.util.*;
import java.io.*;

class Cache {
    public int bs;
    public int associativity;
    public int cachesize;
    public int blocks;
    public int sets;
    public int offsetsize;
    public int indexsize;
    public int tagsize;
    public String[] memory;
    public boolean[][] dirty;
    public boolean[][] valid;
    public int[][] lru;
    public int[][] tags;
    public String[][] bits;
    public boolean isStore = false;
    public String temp = "";

    public Cache(int bs, int associativity, int cachesize) {
        this.bs = bs;
        this.associativity = associativity;
        this.cachesize = cachesize;
        this.blocks = cachesize / bs;
        this.sets = blocks / associativity;
        this.offsetsize = log2(bs);
        this.indexsize = log2(sets);
        this.tagsize = 24 - indexsize - offsetsize;
        this.memory = new String[(1024 * 16 * 1024)];
        this.lru = new int[sets][associativity];
        this.tags = new int[sets][associativity];
        this.bits = new String[sets][associativity];
        this.dirty = new boolean[sets][associativity];
        this.valid = new boolean[sets][associativity];
    }

    public void access(String type, String address, int numbyte, String val) {
        temp = address;
        address = address.substring(2);
        int num = Integer.parseInt(address, 16);
        String bistr = Integer.toBinaryString(num);
        address = extend(bistr);
        String tagnum = address.substring(0, tagsize);
        String indexnum = address.substring(tagsize, tagsize + indexsize);
        String offsetnum = address.substring(tagsize + indexsize);
        int tag = 0;
        int index = 0;
        int offset = 0;
        if (!tagnum.equals("")) {
            tag = Integer.parseInt(tagnum, 2);
        }
        if (!indexnum.equals("")) {
            index = Integer.parseInt(indexnum, 2);
        }
        if (!offsetnum.equals("")) {
            offset = Integer.parseInt(offsetnum, 2);
        }
        int numAddress = num / bs;
        numAddress = numAddress * bs;
        cache(type, tag, index, offset, numbyte, val, numAddress);
    }

    private String extend(String s) {
        for (int i = s.length(); i < 24; i++) {
            s = "0" + s;
        }
        return s;
    }

    private void cache(String type, int tag, int index, int offset, int numbyte, String val, int address) {
        String output = "";
        int loc = hit(tag, index, offset);
        if ((loc != -1) && valid[index][loc]) {
            if (!isStore) {
                String load = bits[index][loc];
                for (int i = 0; i < numbyte; i++) {
                    output += load.substring(offset * 2 + i * 2, offset * 2 + i * 2 + 2);
                }
            } else {
                String store = bits[index][loc];
                output = store.substring(0, offset * 2) + val + store.substring(offset * 2 + 2 * numbyte);
                bits[index][loc] = output;
                dirty[index][loc] = true;
            }
            System.out.print(type + " " + temp + " " + "hit");
            if (!isStore)
                System.out.print(" " + output);
            return;
        }
        boolean check = true;
        for (int i = 0; i < associativity; i++) {
            if (!valid[index][i]) {
                taker(tag, index, i, address);
                check = false;
                break;
            }
        }
        if (check) {
            int i = mover(index);
            String dirt = "clean";
            if (dirty[index][i]) {
                dirt = "dirty";
            }
            String hex = foString(tags[index][i], index);
            taker(tag, index, i, address);
            System.out.println("replacement 0x" + hex + " " + dirt);
        }
        System.out.print(type + " " + temp + " " + "miss");
        loc = hit(tag, index, offset);
        if (!isStore) {
            String load = bits[index][loc];
            for (int i = 0; i < numbyte; i++) {
                output += load.substring(offset * 2 + i * 2, offset * 2 + i * 2 + 2);
            }
        } else {
            String store = bits[index][loc];
            output = store.substring(0, offset * 2) + val + store.substring(offset * 2 + 2 * numbyte);
            bits[index][loc] = output;
            dirty[index][loc] = true;
        }
        if (!isStore) {
            System.out.print(" " + output);
        }
    }

    private String foString(int tag, int index) {
        String off = "";
        while (off.length() < offsetsize) {
            off += "0";
        }
        String tagstr = Integer.toBinaryString(tag);
        String indexstr = Integer.toBinaryString(index);
        while (tagstr.length() < tagsize) {
            tagstr = "0" + tagstr;
        }
        while (indexstr.length() < indexsize) {
            indexstr = "0" + indexstr;
        }
        String bistr = tagstr + indexstr + off;
        if (indexsize == 0) {
            bistr = bistr.substring(0, 24);
        }
        StringBuilder hexstr = new StringBuilder();
        for (int i = 0; i < bistr.length(); i += 4) {
            String hold = bistr.substring(i, i + 4);
            int num = Integer.parseInt(hold, 2);
            hexstr.append(Integer.toHexString(num));
        }
        String s = hexstr.toString();
        while (s.substring(0, 1).equals("0") && s.length() > 1) {
            s = s.substring(1);
        }
        return s;
    }

    private void taker(int tag, int index, int way, int address) {
        dirty[index][way] = false;
        valid[index][way] = true;
        if (memory[address] == null) {
            memory[address] = extender("");
        }
        bits[index][way] = memory[address];
        tags[index][way] = tag;
    }

    private String extender(String s) {
        for (int i = s.length(); i < bs * 2; i++) {
            s = "0" + s;
        }
        return s;
    }

    private int mover(int index) {
        int victim = findvictim(index);
        int i = former(tags[index][victim], index);
        if (dirty[index][victim]) {
            memory[i] = bits[index][victim];
        }
        valid[index][victim] = false;
        lru[index][victim] = 0;
        return victim;
    }

    private int findvictim(int index) {
        int max = Integer.MIN_VALUE;
        int col = 0;
        for (int i = 0; i < associativity; i++) {
            if (lru[index][i] > max) {
                max = lru[index][i];
                col = i;
            }
        }
        return col;
    }

    private int former(int tag, int index) {
        String off = "";
        while (off.length() < offsetsize) {
            off += "0";
        }
        String tagstr = Integer.toBinaryString(tag);
        String indexstr = Integer.toBinaryString(index);
        while (tagstr.length() < tagsize) {
            tagstr = "0" + tagstr;
        }
        while (indexstr.length() < indexsize) {
            indexstr = "0" + indexstr;
        }
        String bistr = tagstr + indexstr + off;
        StringBuilder hexstr = new StringBuilder();
        if (tagsize == 0) {
            bistr = indexstr + off;
        }
        if (indexsize == 0) {
            bistr = tagstr + off;
        }
        if (offsetsize == 0) {
            bistr = tagstr + indexstr;
        }
        for (int i = 0; i < bistr.length(); i += 4) {
            String hold = bistr.substring(i, i + 4);
            int num = Integer.parseInt(hold, 2);
            hexstr.append(Integer.toHexString(num));
        }
        String s = hexstr.toString();
        while (s.substring(0, 1).equals("0") && s.length() > 1) {
            s = s.substring(1);
        }
        return Integer.parseInt(s, 16);
    }

    private int hit(int tag, int index, int offset) {
        int hits = -1;
        for (int i = 0; i < associativity; i++) {
            if (tags[index][i] == tag) {
                hits = i;
                for (int j = 0; j < associativity; j++) {
                    if (j != i) {
                        lru[index][j]++;
                    }
                }
                lru[index][i] = 0;
                break;
            }
        }
        return hits;
    }

    private int log2(int n) {
        int r = 0;
        while (n > 0) {
            n >>= 1;
            r++;
        }
        return r - 1;
    }
}

public class cachesim {
    public static void main(String[] args) throws FileNotFoundException {
        File tracefile = new File(args[0]);
        int cachesize = (Integer.parseInt(args[1]) * 1024);
        int associativity = Integer.parseInt(args[2]);
        int bs = Integer.parseInt(args[3]);
        Cache cache = new Cache(bs, associativity, cachesize);
        Scanner scan = new Scanner(tracefile);
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] parts = line.split(" ");
            String type = parts[0];
            if (type.equals("store")) {
                cache.isStore = true;
            } else {
                cache.isStore = false;
            }
            cache.access(type, parts[1], Integer.parseInt(parts[2]), parts.length > 3 ? parts[3] : "");
            System.out.println();
        }
        scan.close();
        System.exit(0);
    }
}