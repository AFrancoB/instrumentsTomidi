(
var path = thisProcess.nowExecutingPath.dirname;
var synths = path ++ "/instruments/Synths.sc";
s.options.numBuffers = 1024 * 16;
Server.default.options.memSize= 512000*20;
Server.default.options.maxNodes=128*1024;
Server.default.options.numWireBufs= 512;

//Server.default.options.device = "ASIO : MOTU Pro Audio";
//Server.default.options.numOutputBusChannels = 64;
Server.default.options.sampleRate = 48000;
s.boot;

s.waitForBoot({
	synths.load;
});

);
// read it
m = SimpleMIDIFile.read( "~/Desktop/SQ1.mid" );


m.plot
m.info
m.midiEvents
m.countMIDIEvents


//m.addNote(100, 127, 0, 100)

m.midiTrackEvents(0)

m.tempo = 20;

s.meter

(
Pchain(
	// pitch, durs and dyn are taken from the midi file,
	// the instruments and a general volume is also provided
	m.p([
		Pseq([\waveShapeV3, \waveShape, \waveShapeC],inf),
		Pseq([\waveShapeC, \waveShapeV,\waveShapeLPF],inf),
		Pseq([\waveShapeV3,\waveShapeLPF, \waveShapeV],inf),
		Pseq([\waveShapeV,\waveShapeC, \waveShapeV3],inf)
	], amp: 0.1, useTempo: true),

	// this are the parameters native to the instruments, they control timbre, micro-rhythm, micro-pitch, beats, waveshape, etc

	Pbind(
	// parametros solo para el LPF
		\p1, Pwhite(10,20),\p2, Pwhite(3,15),\p3, 10, \p4, 2,\t1, Pwhite(1,20),\t2, Pwhite(5,10),\t3, 4,
	// params para V y V3
		\varTime, 0.0005, \fase, Pseq([0.25pi,0.75pi,1.25pi,1.75pi],inf), \wMin, 0, \wMax, 2000,
	// param para V3
		\offset, #[0,-0.4, 0.75],
	// param para C
		\beats, Pseq([0.1,0.3,0.7,1.4],inf),
	// params que aplican a todos
		\wave, Prand((0..4270),inf), \cents, Pseq([0,0,0,0,25,0,0],inf),
		\out, Prand([10],inf))).trace.play;
{Compander.ar(Pan2.ar(In.ar(10),0).distort,WhiteNoise.ar(0.9),0.5,mul:0.9)}.play;
)

