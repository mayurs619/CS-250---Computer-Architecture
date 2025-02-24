#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

int recursion(int num){
    if (num ==0){
        return 2;
    }
    return (3*num) -(2*recursion(num-1)) +7;
}

int main(int argc, char** argv){
    int temp = atoi(argv[1]);
    temp = recursion(temp);
    printf("%d\n", temp);
    return EXIT_SUCCESS;
}