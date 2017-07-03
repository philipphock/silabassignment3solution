DPUJava MyDPU
{
	Computer = {ASS};
	Index = 501;
	TreeviewGroup = "Tutorial"; // tells you where to find it in SILAB
	Class = de.uulm.silab.tutorial.MyDPU; // path of our java folder.
	v = 0;
};
// we have to include this new JPU include to our configuration file


DPUSGEObject LeftToLeft
{
	Computer = {OPERATOR};
	Index = 60;
	Object = "models.left";
	X = 95.0;
	Y = 6.0;
	Z = 0.0;
	Show = 0;
};
DPUSGEObject LeftToStraight
{
	Computer = {OPERATOR};
	Index = 60;
	Object = "models.straight";
	X = 95.0;
	Y = 6.0;
	Z = 0.0;
	Show = 0;
};
DPUSGEObject LeftToRight
{
	Computer = {OPERATOR};
	Index = 60;
	Object = "models.right";
	X = 95.0;
	Y = 6.0;
	Z = 0.0;
	Show = 0;
};
DPUSGEObject RightToLeft
{
	Computer = {OPERATOR};
	Index = 60;
	Object = "models.left";
	X = 95.0;
	Y = 6.0;
	Z = 0.0;
	Show = 0;
};
DPUSGEObject RightToStraight
{
	Computer = {OPERATOR};
	Index = 60;
	Object = "models.straight";
	X = 95.0;
	Y = 6.0;
	Z = 0.0;
	Show = 0;
};
DPUSGEObject RightToRight
{
	Computer = {OPERATOR};
	Index = 60;
	Object = "models.right";
	X = 95.0;
	Y = 6.0;
	Z = 0.0;
	Show = 0;
};

Connections =	
{	
	VDyn.X -> MyDPU.VDynX,
	VDyn.Y -> MyDPU.VDynY,
	VDyn.s -> MyDPU.VDynS,
	VDyn.v -> MyDPU.v,
	SCNX.LaneIdx -> MyDPU.LaneIndex,
	
	MyDPU.PosLeftX -> LeftToLeft.X,
	MyDPU.PosLeftX -> LeftToRight.X,
	MyDPU.PosLeftX -> LeftToStraight.X,
	MyDPU.PosLeftY -> LeftToLeft.Y,
	MyDPU.PosLeftY -> LeftToRight.Y,
	MyDPU.PosLeftY -> LeftToStraight.Y,
	
	MyDPU.PosRightX -> RightToRight.X,
	MyDPU.PosRightX -> RightToLeft.X,
	MyDPU.PosRightX -> RightToStraight.X,
	MyDPU.PosRightY -> RightToLeft.Y,
	MyDPU.PosRightY -> RightToStraight.Y,
	MyDPU.PosRightY -> RightToRight.Y,
	
	MyDPU.visibleL -> LeftToLeft.Show,
	MyDPU.visibleL -> RightToLeft.Show,
	MyDPU.visibleR -> LeftToRight.Show,
	MyDPU.visibleR -> RightToRight.Show,
	MyDPU.visibleC -> LeftToStraight.Show,
	MyDPU.visibleC -> RightToStraight.Show
	
	
	
};