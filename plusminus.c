#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <cstring>

typedef struct player{
    int total;
    char name[63];
    struct player *next;
} player;
void swap(player* a, player* b) {
    int temp = a->total;
    a->total = b->total;
    b->total = temp;
    char n[63];
    strcpy(n,a->name);
    strcpy(a->name, b->name);
    strcpy(b->name, n);
}
void sortlistn(player *p) {
    bool made_swap = true;
    while (made_swap) {
        made_swap = false;
        player *current = p; 
        while (current->next != NULL) {
            if ((current->next->total) > (current->total)) {
                swap(current, current->next);
                made_swap = true;
            }
            current = current->next;
        }
    }
}
void sortlists(player *p) {
    while(p->next!= NULL){
    if(p->total==p->next->total){
    if(strcmp(p->name,p->next->name)>0){
        swap(p,p->next);
    }
    }
    p=p->next;
    }
}
int main (int argc, char** argv){
    FILE *fp = fopen(argv[1], "r");
    player* current = NULL;
    player* first = NULL;
    char n[63];
    int p = 0;
    int m = 0;
    while (fscanf(fp, "%s\n%d\n%d\n", n, &p, &m) != EOF){
        if (strcmp(n, "DONE") == 0){
            break;
        }
        player* temp=(player*)malloc(sizeof(player));
        strcpy(temp->name, n);
        temp->total = p - m;
        temp->next = NULL;
        
        if (first == NULL) {
            first = temp;
            current = temp;
        } else {
            current->next = temp;
            current = temp;
        }
    }
    fclose(fp); 
    current = first;
    sortlistn(current);
    sortlists(current);
    while (current != NULL) {
        printf("%s\t", current->name);
        printf("%d\n", current->total);
        current = current->next;
    }

    current = first;
    while (current != NULL) {
        player *sub = current;
        current = current->next;
        free(sub);
    }
    return EXIT_SUCCESS;
}