//
// Created by sunnian who I called the Android Performance Master
//

#ifndef PROTO_BITMAPSAVER_H
#define PROTO_BITMAPSAVER_H

void convert_to_bgr(unsigned char* image, int height, int width, int bits_per_pixel);

void convert_bgr_to_rgba(unsigned char* image, int height, int width, int bits_per_pixel);

void write_bitmap_file(unsigned char* image, int height, int width, char* imageFileName, int bits_per_pixel);

#endif //PROTO_BITMAPSAVER_H
