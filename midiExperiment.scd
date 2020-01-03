(
var path = thisProcess.nowExecutingPath.dirname;
var synths = path ++ "/instruments/Synths.sc";
var midis = path ++ "/midiFiles";
s.options.numBuffers = 1024 * 16;
Server.default.options.memSize= 512000*20;
Server.default.options.maxNodes=128*1024;
Server.default.options.numWireBufs= 512;

//Server.default.options.device = "ASIO : MOTU Pro Audio";
//Server.default.options.numOutputBusChannels = 64;
Server.default.options.sampleRate = 48000;
s.boot;

s.waitForBoot({
	synths.load; // carga los synths
	~instruments = Dictionary.new; // diccionario de partichelas

	// añadir partichelas
	~instruments.add(\vln1 -> SimpleMIDIFile.read( midis++"/SQ1-Violin_I.mid" ));
	~instruments.add(\vln2 -> SimpleMIDIFile.read( midis++"/SQ1-Violin_II.mid" ));
	~instruments.add(\vla  -> SimpleMIDIFile.read( midis++"/SQ1-Viola.mid" ));
	~instruments.add(\vc   -> SimpleMIDIFile.read( midis++"/SQ1-Violoncello.mid" ));
});

);
// read it

~instruments // a dictionary that contains all the instruments

~instruments.at(\vc).p.play // access violin 1

~instruments.plot
~instruments.info
~instruments.midiEvents
~instruments.countMIDIEvents


//m.addNote(100, 127, 0, 100)

~instruments.midiTrackEvents(0)

~instruments.tempo = 20;

(// violin 1
~vln1= Pchain(
	// pitch, durs and dyn are taken from the midi file,
	// the instruments and a general volume is also provided
	~instruments.at(\vln1).p([\waveShapeV3], amp: 0.025, useTempo: true),

	// this are the parameters native to the instruments, they control timbre, micro-rhythm, micro-pitch, beats, waveshape, etc

	Pbind(
	// params para V y V3
		\varTime, 0.0005, \fase, Pseq([0.25pi,0.75pi,1.25pi,1.75pi],inf), \wMin, 0, \wMax, 2000,
	// param para V3
		\offset, #[0,-0.4, 0.75],
	// params que aplican a todos
		\wave, Prand((0..4270),inf), \cents, Pseq([0,0,0,0,25,0,0],inf),
		\out, Prand([10],inf))).trace;
);

(// violin 2
~vln2= Pchain(
	// pitch, durs and dyn are taken from the midi file,
	// the instruments and a general volume is also provided
	~instruments.at(\vln2).p([\waveShapeV], amp: 0.025, useTempo: true),

	// this are the parameters native to the instruments, they control timbre, micro-rhythm, micro-pitch, beats, waveshape, etc

	Pbind(
	// params para V y V3
		\varTime, 0.0005, \fase, Pseq([0.25pi,0.75pi,1.25pi,1.75pi],inf), \wMin, 0, \wMax, 2000,
	// params que aplican a todos
		\wave, Prand((0..4270),inf), \cents, Pseq([0,0,0,0,25,0,0],inf),
		\out, Prand([10],inf))).trace;
);

(// Viola
~vla= Pchain(
	// pitch, durs and dyn are taken from the midi file,
	// the instruments and a general volume is also provided
	~instruments.at(\vla).p([\waveShapeC], amp: 0.025, useTempo: true),

	// this are the parameters native to the instruments, they control timbre, micro-rhythm, micro-pitch, beats, waveshape, etc

	Pbind(
	// param para C
		\beats, Pseq([0.1,0.3,0.7,1.4],inf),
	// params que aplican a todos
		\wave, Prand((0..4270),inf), \cents, Pseq([0,0,0,0,25,0,0],inf),
		\out, Prand([10],inf))).trace;
);

(// Cello
~vc= Pchain(
	// pitch, durs and dyn are taken from the midi file,
	// the instruments and a general volume is also provided
	~instruments.at(\vc).p([\waveShapeLPF], amp: 0.025, useTempo: true),

	// this are the parameters native to the instruments, they control timbre, micro-rhythm, micro-pitch, beats, waveshape, etc

	Pbind(
	// parametros solo para el LPF
		\p1, Pwhite(1,20),\p2, Pwhite(3,15),\p3, 10, \p4, 2,\t1, Pwhite(1,20),\t2, Pwhite(5,10),\t3, 4,
	// params que aplican a todos
		\wave, Prand((0..4270),inf), \cents, Pseq([0,0,0,0,25,0,0],inf),
		\out, Prand([10],inf))).trace;
);


(// it will play once each file
~compressor= Synth(\compander);
~vln1.play;
~vln2.play;
~vla.play;
~vc.play;
);

~compressor.set(\gate, 0) // this will turn off the compressor and will not filtrate the signals


// no olvides prender el compresor para sonar esto de abajo:
~compressor= Synth(\compander);

// para cambiar en tiempo real los parametros de un instrumento
Pdef(\sq).play

// para escuchar solamente un instrumento
Pdef(\sq, ~vln1)

// escuchar sets de instrumentos parciales, loops infinitos
Pdef(\sq, Ppar([~vln1, ~vln2, ~vla, ~vc],inf))



s.meter

