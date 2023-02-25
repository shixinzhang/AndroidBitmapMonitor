//
// Created by sunnian who I called the Android Performance Master
//

#ifndef PROTO_BITMAPSAVER_H
#define PROTO_BITMAPSAVER_H

void convert_to_bgr(unsigned char* image, unsigned int height, unsigned int width, unsigned int bits_per_pixel);

void convert_bgr_to_rgba(unsigned char* image, unsigned int height, unsigned int width, unsigned int bits_per_pixel);

void write_bitmap_file(unsigned char* image, unsigned int height, unsigned int width, char* imageFileName, unsigned int bits_per_pixel);

#endif //PROTO_BITMAPSAVER_H
