#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

int main(int argc, char** argv){
    int total = atoi(argv[1]);
    int* ptr =(int*)malloc(total * sizeof(int));
    *ptr = 1;
    int count = 0;

    while(count<total){
        if(count == 0){
            printf("%d\n", *ptr);
        }
        if(count ==1){
            ptr++;
            *ptr = 1;
            printf("%d\n", *ptr);
        }
        if (count ==2){
            ptr++;
            *ptr = 2;
            printf("%d\n", *ptr);
        }
        if (count >= 3){
            int temp =0;
            ptr++;
            ptr-=3;
            temp+=*ptr;
            ptr++;
            temp+=*ptr;
            ptr++;
            temp+=*ptr;
            ptr++;
            *ptr = temp;
            printf("%d\n", *ptr);
        }
        count++;
    }
    ptr = ptr - total + 1;
    free(ptr);
    /*
    int num = atoi(argv[1]);
    int first = 1;
    int second = 1;
    int third = 2;
    if (num>=1){
        printf("%d", first);
    }
    if (num>=2){
        printf("%d", second);
    }
    if (num>=3){
        printf("%d", third);
    }
    for (int i = 4; i<num; i++){
        int current = first+second+third;
        printf("%d", current);
        first = second;
        second =third;
        third = current;
    }
    */
    return EXIT_SUCCESS;
}