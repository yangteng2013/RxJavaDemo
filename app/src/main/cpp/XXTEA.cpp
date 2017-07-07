//
// Created by lijie on 2017/7/6.
//

#include "XXTEA.h"

typedef uint32_t xxtea_uint;

static const std::string base64_chars =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                "abcdefghijklmnopqrstuvwxyz"
                "0123456789+/";

static inline bool is_base64(unsigned char c) {
    return (isalnum(c) || (c == '+') || (c == '/'));
}

std::string base64_encode(unsigned char const *bytes_to_encode, unsigned int in_len) {
    std::string ret;
    int i = 0;
    int j = 0;
    unsigned char char_array_3[3];
    unsigned char char_array_4[4];

    while (in_len--) {
        char_array_3[i++] = *(bytes_to_encode++);
        if (i == 3) {
            char_array_4[0] = (char_array_3[0] & 0xfc) >> 2;
            char_array_4[1] = ((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >> 4);
            char_array_4[2] = ((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >> 6);
            char_array_4[3] = char_array_3[2] & 0x3f;

            for (i = 0; (i < 4); i++)
                ret += base64_chars[char_array_4[i]];
            i = 0;
        }
    }

    if (i) {
        for (j = i; j < 3; j++)
            char_array_3[j] = '\0';

        char_array_4[0] = (char_array_3[0] & 0xfc) >> 2;
        char_array_4[1] = ((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >> 4);
        char_array_4[2] = ((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >> 6);
        char_array_4[3] = char_array_3[2] & 0x3f;

        for (j = 0; (j < i + 1); j++)
            ret += base64_chars[char_array_4[j]];

        while ((i++ < 3))
            ret += '=';

    }
    return ret;
}

std::string base64_decode(std::string const &encoded_string) {
    int in_len = encoded_string.size();
    int i = 0;
    int j = 0;
    int in_ = 0;
    unsigned char char_array_4[4], char_array_3[3];
    std::string ret;

    while (in_len-- && (encoded_string[in_] != '=') && is_base64(encoded_string[in_])) {
        char_array_4[i++] = encoded_string[in_];
        in_++;
        if (i == 4) {
            for (i = 0; i < 4; i++)
                char_array_4[i] = base64_chars.find(char_array_4[i]);

            char_array_3[0] = (char_array_4[0] << 2) + ((char_array_4[1] & 0x30) >> 4);
            char_array_3[1] = ((char_array_4[1] & 0xf) << 4) + ((char_array_4[2] & 0x3c) >> 2);
            char_array_3[2] = ((char_array_4[2] & 0x3) << 6) + char_array_4[3];

            for (i = 0; (i < 3); i++)
                ret += char_array_3[i];
            i = 0;
        }
    }

    if (i) {
        for (j = i; j < 4; j++)
            char_array_4[j] = 0;

        for (j = 0; j < 4; j++)
            char_array_4[j] = base64_chars.find(char_array_4[j]);

        char_array_3[0] = (char_array_4[0] << 2) + ((char_array_4[1] & 0x30) >> 4);
        char_array_3[1] = ((char_array_4[1] & 0xf) << 4) + ((char_array_4[2] & 0x3c) >> 2);
        char_array_3[2] = ((char_array_4[2] & 0x3) << 6) + char_array_4[3];

        for (j = 0; (j < i - 1); j++) ret += char_array_3[j];
    }

    return ret;
}

static unsigned char *fix_key_length(unsigned char *key, xxtea_uint key_len) {
    unsigned char *tmp = (unsigned char *) malloc(16);
    memcpy(tmp, key, key_len);
    memset(tmp + key_len, '\0', 16 - key_len);
    return tmp;
}

static xxtea_uint *
xxtea_to_uint_array(unsigned char *data, xxtea_uint len, int include_length, xxtea_uint *ret_len) {
    xxtea_uint i, n, *result;

    n = len >> 2;
    n = (((len & 3) == 0) ? n : n + 1);
    if (include_length) {
        result = (xxtea_uint *) malloc((n + 1) << 2);
        result[n] = len;
        *ret_len = n + 1;
    } else {
        result = (xxtea_uint *) malloc(n << 2);
        *ret_len = n;
    }
    memset(result, 0, n << 2);
    for (i = 0; i < len; i++) {
        result[i >> 2] |= (xxtea_uint) data[i] << ((i & 3) << 3);
    }

    return result;
}

static void xxtea_uint_encrypt(xxtea_uint *v, xxtea_uint len, xxtea_uint *k) {
    xxtea_uint n = len - 1;
    xxtea_uint z = v[n], y = v[0], p, q = 6 + 52 / (n + 1), sum = 0, e;
    if (n < 1) {
        return;
    }
    while (0 < q--) {
        sum += XXTEA_DELTA;
        e = sum >> 2 & 3;
        for (p = 0; p < n; p++) {
            y = v[p + 1];
            z = v[p] += XXTEA_MX;
        }
        y = v[0];
        z = v[n] += XXTEA_MX;
    }
}

static unsigned char *
xxtea_to_byte_array(xxtea_uint *data, xxtea_uint len, int include_length, xxtea_uint *ret_len) {
    xxtea_uint i, n, m;
    unsigned char *result;

    n = len << 2;
    if (include_length) {
        m = data[len - 1];
        if ((m < n - 7) || (m > n - 4)) return NULL;
        n = m;
    }
    result = (unsigned char *) malloc(n + 1);
    for (i = 0; i < n; i++) {
        result[i] = (unsigned char) ((data[i >> 2] >> ((i & 3) << 3)) & 0xff);
    }
    result[n] = '\0';
    *ret_len = n;

    return result;
}

static unsigned char *
do_xxtea_encrypt(unsigned char *data, xxtea_uint len, unsigned char *key, xxtea_uint *ret_len) {
    unsigned char *result;
    xxtea_uint *v, *k, v_len, k_len;

    v = xxtea_to_uint_array(data, len, 1, &v_len);
    k = xxtea_to_uint_array(key, 16, 0, &k_len);
    xxtea_uint_encrypt(v, v_len, k);
    result = xxtea_to_byte_array(v, v_len, 0, ret_len);
    free(v);
    free(k);

    return result;
}


unsigned char *
xxtea_encrypt(unsigned char *data, xxtea_uint data_len, unsigned char *key, xxtea_uint key_len,
              xxtea_uint *ret_length) {
    unsigned char *result;

    *ret_length = 0;

    if (key_len < 16) {
        unsigned char *key2 = fix_key_length(key, key_len);
        result = do_xxtea_encrypt(data, data_len, key2, ret_length);
        free(key2);
    } else {
        result = do_xxtea_encrypt(data, data_len, key, ret_length);
    }

    return result;
}

// xxtea加密
std::string xxtea_encrypt(std::string data, std::string key) {
    unsigned char date_uchar[1024];
    strcpy((char *) date_uchar, (const char *) data.c_str());
    unsigned char key_uchar[1024];
    strcpy((char *) key_uchar, (const char *) key.c_str());
    xxtea_uint s[1];
    unsigned char *encrypt = xxtea_encrypt(date_uchar, strlen((const char *) date_uchar), key_uchar,
                                           strlen((const char *) key_uchar), s);
    std::string encoded = base64_encode(encrypt, s[0]);
    return encoded;
}

static void xxtea_uint_decrypt(xxtea_uint *v, xxtea_uint len, xxtea_uint *k) {
    xxtea_uint n = len - 1;
    xxtea_uint z = v[n], y = v[0], p, q = 6 + 52 / (n + 1), sum = q * XXTEA_DELTA, e;
    if (n < 1) {
        return;
    }
    while (sum != 0) {
        e = sum >> 2 & 3;
        for (p = n; p > 0; p--) {
            z = v[p - 1];
            y = v[p] -= XXTEA_MX;
        }
        z = v[n];
        y = v[0] -= XXTEA_MX;
        sum -= XXTEA_DELTA;
    }
}

static unsigned char *
do_xxtea_decrypt(unsigned char *data, xxtea_uint len, unsigned char *key, xxtea_uint *ret_len) {
    unsigned char *result;
    xxtea_uint *v, *k, v_len, k_len;

    v = xxtea_to_uint_array(data, len, 0, &v_len);
    k = xxtea_to_uint_array(key, 16, 0, &k_len);
    xxtea_uint_decrypt(v, v_len, k);
    result = xxtea_to_byte_array(v, v_len, 1, ret_len);
    free(v);
    free(k);

    return result;
}

unsigned char *
xxtea_decrypt(unsigned char *data, xxtea_uint data_len, unsigned char *key, xxtea_uint key_len,
              xxtea_uint *ret_length) {
    unsigned char *result;

    *ret_length = 0;

    if (key_len < 16) {
        unsigned char *key2 = fix_key_length(key, key_len);
        result = do_xxtea_decrypt(data, data_len, key2, ret_length);
        free(key2);
    } else {
        result = do_xxtea_decrypt(data, data_len, key, ret_length);
    }

    return result;
}

// xxtea 解密
std::string xxtea_decrypt(std::string data, std::string key) {
    std::string decoded = base64_decode(data);
    unsigned char date_uchar[1024];
    memcpy(date_uchar, decoded.c_str(), decoded.length());
    date_uchar[decoded.length()] = '\0';
    unsigned char key_uchar[1024];
    strcpy((char *) key_uchar, (const char *) key.c_str());
    xxtea_uint s[1];
    unsigned char *encrypt = xxtea_decrypt(date_uchar, decoded.length(), key_uchar,
                                           strlen((const char *) key_uchar), s);
    if (!encrypt) {
        return std::string("false_false");
    }
    std::string result = (char *) encrypt;
    return result;
}


