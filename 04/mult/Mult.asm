// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

//

// Put your code here.
@RAMS //CurrentRams
M=0
@R2
M=0
(MAGIC)//LABEL 

//Decrement For Loop
@R0
M=M-1

//Check Loop Eligibility
@R0
D=-M
@END
D;JGT

//Store R1
@R1
D=M

//Add R2+=R1
@R2
M=M+D


@MAGIC 
0;JMP
(END)
@END
0;JMP





