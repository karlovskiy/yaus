package info.karlovskiy.yaus.util;

import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class Base62 {

    private static final char[] BASE62_ALPHABET = {
//  index    0    1    2    3    4    5    6    7    8    9
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
//  ascii    48   49   50   51   52   53   54   55   56   57

//  index    10   11   12   13   14   15   16   17   18   19   20   21   22
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
//  ascii    97   98   99  100  101  102  103  104  105  106  107  108  109

//  index    23   24   25   26   27   28   29   30   31   32   33   34   35
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
//  ascii   110  111  112  113  114  115  116  117  118  119  120  121  122

//  index    36   37   38   39   40   41   42   43   44   45   46   47   48
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
//  ascii    65   66   67   68   69   70   71   72   73   74   75   76   77

//  index    49   50   51   52   53   54   55   56   57   58   59   60   61
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
//  ascii    78   79   80   81   82   83   84   85   86   87   88   89   90
    };

    private static final Pattern BASE62_ALPHABET_REGEX = Pattern.compile("[" + new String(BASE62_ALPHABET) + "]+");

    /**
     * Encode id to string
     *
     * @param id id
     * @return string
     */
    public static String encode(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Incorrect id for encode");
        }
        if (id == 0) {
            return "0";
        }
        val builder = new StringBuilder();
        while (id > 0) {
            int remainder = (int) (id % 62);
            id /= 62;
            builder.insert(0, BASE62_ALPHABET[remainder]);
        }
        return builder.toString();
    }

    /**
     * Decode char sequence to id
     *
     * @param val char sequence
     * @return id
     */
    public static long decode(CharSequence val) {
        if (StringUtils.isEmpty(val)) {
            throw new IllegalArgumentException("Empty value for decode");
        }
        val matcher = BASE62_ALPHABET_REGEX.matcher(val);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Incorrect value for decode");
        }
        long result = 0;
        long counter = 1;
        for (int i = val.length() - 1; i >= 0; i--) {
            char c = val.charAt(i);
            int index;
            if (c >= 'a' && c <= 'z') {
                index = c - 87; /* 97(ascii) - 10(index) */
            } else if (c >= 'A' && c <= 'Z') {
                index = c - 29; /* 65(ascii) - 36(index) */
            } else if (c >= '0' && c <= '9') {
                index = c - 48; /* 48(ascii) -  0(index) */
            } else {
                throw new IllegalArgumentException("Illegal character: " + c);
            }
            result += index * counter;
            counter *= 62;
        }
        return result;
    }
}
