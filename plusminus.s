.data
newline: .asciiz "\n"
space: .asciiz " "
player_done: .asciiz "DONE"
PN: .asciiz "Player name:"
PP: .asciiz "Points scored by player's team while on court:"
PM: .asciiz "Points scored by opposing team while on court:"

.text
.globl main

main:
    addi $sp, $sp, -28
    sw $ra 0($sp)  
    sw $s0 4($sp)
    sw $s1 8($sp)
    sw $s2 12($sp)
    sw $s3 16($sp)
    sw $s4 20($sp)
    sw $s5 24($sp)

function:
    la $a0, PN
    li $v0, 4
    syscall
    li $a1, 64
    move $a0, $a1
    li $v0, 9
    syscall
    move $a0, $v0
    li $v0, 8
    syscall
    jal remove
    move $s0, $v0
    move $a0, $s0
    la $a1, player_done
    jal cmp
    beqz $v0, printlist
    la $a0, PP
    li $v0, 4
    syscall
    li $v0, 5
    syscall
    move $s4, $v0
    la $a0, PM
    li $v0, 4
    syscall
    li $v0, 5
    syscall
    move $s2, $v0
    sub $s4, $s4, $s2
    li $a0, 72
    li $v0, 9
    syscall
    move $s2, $v0
    sw $s0, 0($s2)
    sw $s4, 64($s2)
    beqz $s3, updateh
    move $s1, $0
    move $s5, $s3
    beqz $s3, updateh

sortlist:
    lw $t1, 64($s3)
    lw $t2, 64($s2)
    bne, $t1, $t2, sorter
    bgt, $t2, $t1, updateh
    lw $a0, 0($s3)
    lw $a1, 0($s2)
    jal strcmp
    move $t3, $v0
    bgt $t3, 0, updateh

sorter:
    lw $t0, 64($s5)
    bgt $s4, $t0, before
    blt $s4, $t0, mover
    lw $a0, 0($s2)
    lw $a1, 0($s5)
    jal strcmp
    move $t3, $v0
    slt, $t4, $t3, 0
    bne, $t4, $0, before

mover:
    move $s1, $s5
    lw $s5, 68($s5)
    j done

before:
    sw $s5, 68($s2)
    beq $s1, $0, updateh
    sw $s2, 68($s1)
    j function

updateh:
    sw $s3, 68($s2)
    move $s3, $s2
    j function

done:
    beqz $s5, before
    j sortlist

remove:
    addi $sp,$sp,-4
    sw $ra,0($sp)
    move $t2, $a0

rloop:    
    lb $t0, 0($t2)
    beq $t0, $zero, fin
    la $t1, newline
    lb $t1, 0($t1)
    bne $t0, $t1, newline_count
    sb $zero, 0($t2)
   
newline_count:
    addi $t2, $t2, 1
    j rloop
   
fin:
    move $v0, $a0
    lw $ra, 0($sp)
    addi $sp, $sp,4
    jr $ra

cmp:
    addi $sp, $sp, -4
    sw $ra, 0($sp)

mov:
    lb $t0, 0($a0)
    lb $t1, 0($a1)
    beq $t0, $zero, end_cmp
    bne $t0, $t1, fail          
    addi $a0, $a0, 1                 
    addi $a1, $a1, 1
    j mov                        

fail:
    li $v0, 1
    lw $ra, 0($sp)
    addi $sp, $sp, 4
    jr $ra 

end_cmp:
    li $v0, 0
    lw $ra, 0($sp)
    addi $sp, $sp, 4
    jr $ra

strcpy:
    lb $t0, 0($a1)
    beq $t0, $zero, done_copying
    sb $t0, 0($a0)
    addi $a0, $a0, 1
    addi $a1, $a1, 1
    j strcpy

done_copying:
    jr $ra

strcmp:
    lb $t0, 0($a0)
    lb $t1, 0($a1)
    bne $t0, $t1, done_with_strcmp_loop
    addi $a0, $a0, 1
    addi $a1, $a1, 1
    bnez $t0, strcmp
    li $v0, 0
    jr $ra

done_with_strcmp_loop:
    sub $v0, $t0, $t1
    jr $ra

printlist:
    lw $a0,0($s3)
    li $v0, 4
    syscall
    la $a0,space
    li $v0, 4
    syscall
    lw $a0, 64($s3)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    lw $s3, 68($s3)
    beqz $s3, stop
    j printlist
   
stop:
    lw $ra 0($sp)
    lw $s3 4($sp)
    lw $s0 8($sp)
    lw $s4 12($sp)
    lw $s2 16($sp)
    lw $s5 20($sp)
    lw $s1 24($sp)
    addi $sp,$sp, 28
    jr $ra    
   