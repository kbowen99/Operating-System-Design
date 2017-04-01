(DOBLACK)
	@24576	//infallible keyboard Register
	D=M
@DOWHITE	//Whiten if not pressed
D;JEQ
	@24575 //Keyboard - 1
	D=M
@DOWHITE //If White
D;JLT
	@CURRPIX
	D=M
	@16384	//infallible Screen
	D=A+D
	A=D
	M=-1
	@CURRPIX
	M=M+1
@DOBLACK
0;JMP//All Loops are infinite... (if pressed)

(DOWHITE)
	@24576	//Handy Dandy Keyboard
	D=M
@DOBLACK	//if pressed, Do Black
D;JGT
	@CURRPIX
	D=M
	@16384	//Screen
	D=A+D
	A=D
	M=0
	@CURRPIX
	M=M-1	//change current pixel
@DOWHITE
0;JMP