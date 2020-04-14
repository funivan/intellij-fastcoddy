package org.funivan.intellij.FastCoddy.Helper;

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
public class Str {

    public static String plural(int num, String singular, String plural) {
        if (num == 1) {
            return Integer.toString(num) + ' ' + singular;
        }
        return Integer.toString(num) + ' ' + plural;
    }

}
