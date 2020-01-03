//Synths
// execute here to compile every function and synthdef

//load wavetable catalogue;
var path = thisProcess.nowExecutingPath.dirname;
var wavsCatalogue = path++"/wavetable";
~variable= { |min= 102, max= 400|
	var mul= (max - min)/2;
	var add= min + mul;

	(mul: mul, addi: add)
};

~w= Array.new(44);

~wavList = ["_aguitar","_altosax","_birds","_bitreduced","_blended","_bw_saw","_bw_sawbright","_bw_sawgap","_bw_sawrounded","_bw_sin","_bw_sq","_bw_sqrounded","_bw_tri","_c604","_cello","_clarinett","_clavinet","_dbass","_distorted","_ebass","_eguitar","_eorgan","_epiano","_flute","_fmsynth","_granular","_hdrawn","_hvoice","_oboe","_oscchip","_overtone","_piano","_pluckalgo","_raw","_sinharm","_snippets","_stereo","_stringbox","_symetric","_theremin","_vgame","_vgamebasic","_violin"];

(~wavList.size+1).do{|i|
	var akwf= "/AKWF", list= ~wavList;

//	(wavsCatalogue++akwf++list[i]++"/*").postln;
	if(i==0,
		{~w.insert(i,SoundFile.collectIntoBuffers(wavsCatalogue++akwf++"/*"))},
		{~w.insert(i,SoundFile.collectIntoBuffers(wavsCatalogue++akwf++list[i-1]++"/*",s))}
	);

};

~waveShapeList= ["AKWF"]++~wavList;

~printOscs= ~waveShapeList.size.do{|i|  ~waveShapeList[i].postln };

(
~osciladores = Dictionary.new;

~w.size.do{|i|
	~osciladores.add(~waveShapeList[i].asSymbol ->
		(
			min: ~w[i][0].bufnum,
			max: ~w[i][~w[i].size-1].bufnum)
	).postln;
}
);

(// check all the ranges and the oscilators
~osciladores.size;
~w.size.do{|i| ~osciladores.at(~waveShapeList[i].asSymbol).postln}
);


// Synthdefs

// el catálogo de waveshapes sin ningún tipo de alteración
SynthDef(\waveShape, {
	| wave=0, freq=300, cents=0, amp=1, att=1, dec=0.1, susTime=1, rel=1, gate=1, out=0 |
	var sig, env;
	sig= Osc.ar(wave, freq*(cents*0.01).midiratio, 0, amp);
	env= EnvGen.kr(
		Env.adsr(attackTime:att, decayTime:dec, sustainLevel: susTime, releaseTime: rel),
		gate, doneAction:2);
	Out.ar(out, sig*env)
}).add;
// first test, change the waveshape with the param wave!
/*(instrument: \waveShape, wave: 4500).play;*/

// test the cents 50 cents will produce 1/4 tones, 66.6667 will produce 1/3, etc
/*Pbind(\instrument, \waveShape, \wave, Prand((0..200),inf), \dur, 2, \midinote, Pseq([60,60,67,67],inf), \cents, Pseq([0,66.666],inf)).play;*/

// BTW the last waveshape buffer number should be:

/*(instrument: \waveShape, wave: 4270).play*/


// el catálogo de waveshapes con LPF
SynthDef(\waveShapeLPF, {
	| wave=0, freq=300, cents=0, rq=0.1, amp=1, att=1, dec=0.1, susTime=1, rel=1, gate=1, out=0,
	p1=5,p2=8, p3=13, p4=5, t1=0.2, t2=0.2, t3=0.2, vibr=0, wide=0.5
	|
	var sig, trem, filtro, env;
	filtro= EnvGen.kr(Env([p1,p2,p3,p4],[t1,t2,t3]));
	trem= SinOsc.ar(vibr, 0, wide, 1);
	sig= Osc.ar(wave, freq*(cents*0.01).midiratio, 0, 1)*trem;
	sig= RLPF.ar(sig,freq*filtro, rq, amp);
	env= EnvGen.kr(
		Env.adsr(attackTime:att, decayTime:dec, sustainLevel: susTime, releaseTime: rel),
		gate, doneAction:2);
	Out.ar(out, sig*env)
}).add;

/*(
Pbind(\instrument, \waveShapeLPF, \wave, 550, \dur, 2, \midinote, Pseq([60,67,68],inf),
	\p1, 10, \p2, 1, \p3, 10, \p4, 5, \t1, 0.4, \t2, 0.4, \t3, 1.2, \vibr, Pseq([13,5,1,0],inf)).trace.play
);*/


// el catálogo de waveshapes con LPF
SynthDef(\waveShapeHPF, {
	| wave=0, freq=300, cents=0, lpfFreq=5, rq=0.1, amp=1, att=1, dec=0.1, susTime=1, rel=1, gate=1, out=0,
	p1=1,p2=2, p3=3, p4=1, t1=0.2, t2=0.2, t3=0.2, vibr=0, wide=0.5
	|
	var sig, trem, filtro, env;
	filtro= EnvGen.kr(Env([p1,p2,p3,p4],[t1,t2,t3]));
	trem= SinOsc.ar(vibr, 0, wide, 1);
	sig= Osc.ar(wave, freq*(cents*0.01).midiratio, 0, 1)*trem;
	sig= RHPF.ar(sig,freq*filtro, 0.1, amp);
	env= EnvGen.kr(
		Env.adsr(attackTime:att, decayTime:dec, sustainLevel: susTime, releaseTime: rel),
		gate, doneAction:2);
	Out.ar(out, sig*env)
}).add;

/*( // try the two together with different waveshapes!!!
Pbind(\instrument, \waveShapeLPF, \wave, 2790, \dur, 2, \midinote, Pseq([60,67,68],inf),
	\p1, 10, \p2, 1, \p3, 10, \p4, 5, \t1, 0.4, \t2, 0.4, \t3, 1.2, \vibr, Pseq([13,5,1,0],inf)).trace.play
);
(
Pbind(\instrument, \waveShapeHPF, \wave, 2290, \dur, 2, \midinote, Pseq([60,67,68],inf),
	\p1, 10, \p2, 1, \p3, 10, \p4, 5, \t1, 0.4, \t2, 0.4, \t3, 1.2, \vibr, Pseq([13,5,1,0],inf)).trace.play
);*/



// waveshapes con chorus
SynthDef(\waveShapeC, {
	| wave=1, freq=300, cents=0, beats=0.5, amp=1, att=1, dec=0.1, susTime=1, rel=1, gate=1, out=0 |
	var sig, env;
	sig= COsc.ar(wave, freq*(cents*0.01).midiratio, beats, amp);
	env= EnvGen.kr(
		Env.adsr(attackTime:att, decayTime:dec, sustainLevel: susTime, releaseTime: rel),
		gate, doneAction:2);
	Out.ar(out, sig*env)
}).add;

//Pbind(\instrument, \waveShapeC, \dur, 2, \midinote, Pseq([60, 67, 68],inf)).trace.play


// waveshapes que pueden interpolar entre unas y otras
SynthDef(\waveShapeV, {
	| varTime=15, fase=1, wMin=0, wMax= 100, freq=300, cents=0, amp=1, att=1, dec=0.1, susTime=1, rel=1, gate=1, out=0 |
	var vars, osc, sig, env;
	vars= ~variable.(wMin, wMax);
	osc= SinOsc.ar(varTime, fase, vars.mul, vars.addi);
	sig= VOsc.ar(osc, freq*(cents*0.01).midiratio, 0, amp);
	env= EnvGen.kr(
		Env.adsr(attackTime:att, decayTime:dec, sustainLevel: susTime, releaseTime: rel),
		gate, doneAction:2);
	Out.ar(out, sig*env)
}).add;

// Pbind(\instrument, \waveShapeV, \dur, 2, \varTime, 0.0005, \fase, Pseq([0.25pi,0.75pi,1.25pi,1.75pi],inf), \wMin, 0, \wMax, 2000, \midinote, Pseq([60, 67, 68],inf)).trace.play

// waveshapes que pueden interpolar entre unas y otras, tres frequencias simultaneas
SynthDef(\waveShapeV3, {
	| varTime=15, fase=1, wMin=0, wMax= 100, freq=300, cents=0, offset=#[0,0,0], amp=1, att=1, dec=0.1, susTime=1, rel=1, gate=1, out=0 |
	var vars, osc, pitch, sig, env;
	pitch= freq*(cents*0.01).midiratio;
	vars= ~variable.(wMin, wMax);
	osc= SinOsc.ar(varTime, fase, vars.mul, vars.addi);
	sig= VOsc3.ar(osc, pitch+offset[0], pitch+offset[1], pitch+offset[2], amp);
	env= EnvGen.kr(
		Env.adsr(attackTime:att, decayTime:dec, sustainLevel: susTime, releaseTime: rel),
		gate, doneAction:2);
	Out.ar(out, sig*env)
}).add;

/*Pbind(\instrument, \waveShapeV3, \dur, 2, \varTime, 0.05, \fase, Pseq([0.25pi,0.75pi,1.25pi,1.75pi],inf), \wMin, ~osciladores.at(\_ebass).min, \wMax, ~osciladores.at(\_ebass).max, \midinote, Pseq([60, 67, 68],inf), \offset, [0,1.5,-2.7]).trace.play*/

// Percusive Envelope Synths, los mismos synths pero con envolvente percusive, los argumentos \att y \rel controlan el attaque y el release respectivamente independientemente del tiempo de onset de cada evento

// el catálogo de waveshapes sin ningún tipo de alteración
SynthDef(\waveShapePerc, {
	| wave=0, freq=300, cents=0, amp=1, att=0.001, rel=1.5, gate=1, out=0 |
	var sig, env;
	sig= Osc.ar(wave, freq*(cents*0.01).midiratio, 0, amp);
	env= EnvGen.kr(
		Env.perc(att,rel),
		gate, doneAction:2);
	Out.ar(out, sig*env)
}).add;
// first test, change the waveshape with the param wave!


// el catálogo de waveshapes con LPF
SynthDef(\waveShapeLPFPerc, {
	| wave=0, freq=300, cents=0, rq=0.1, amp=1, att=0.001, rel=1.5, gate=1, out=0,
	p1=5,p2=8, p3=13, p4=5, t1=0.2, t2=0.2, t3=0.2, vibr=0, wide=0.5
	|
	var sig, trem, filtro, env;
	filtro= EnvGen.kr(Env([p1,p2,p3,p4],[t1,t2,t3]));
	trem= SinOsc.ar(vibr, 0, wide, 1);
	sig= Osc.ar(wave, freq*(cents*0.01).midiratio, 0, 1)*trem;
	sig= RLPF.ar(sig,freq*filtro, rq, amp);
	env= EnvGen.kr(
		Env.perc(att,rel),
		gate, doneAction:2);
	Out.ar(out, sig*env)
}).add;

/*(
Pbind(\instrument, \waveShapeLPFPerc, \wave, 550, \dur, 2, \midinote, Pseq([60,67,68],inf),
	\p1, 10, \p2, 1, \p3, 10, \p4, 5, \t1, 0.4, \t2, 0.4, \t3, 1.2, \vibr, Pseq([13,5,1,0],inf)).trace.play
);*/


// el catálogo de waveshapes con LPF
SynthDef(\waveShapeHPFPerc, {
	| wave=0, freq=300, cents=0, lpfFreq=5, rq=0.1, amp=1, att=0.001, rel=1.5, gate=1, out=0,
	p1=1,p2=2, p3=3, p4=1, t1=0.2, t2=0.2, t3=0.2, vibr=0, wide=0.5
	|
	var sig, trem, filtro, env;
	filtro= EnvGen.kr(Env([p1,p2,p3,p4],[t1,t2,t3]));
	trem= SinOsc.ar(vibr, 0, wide, 1);
	sig= Osc.ar(wave, freq*(cents*0.01).midiratio, 0, 1)*trem;
	sig= RHPF.ar(sig,freq*filtro, 0.1, amp);
	env= EnvGen.kr(
		Env.perc(att,rel),
		gate, doneAction:2);
	Out.ar(out, sig*env)
}).add;

/*( // try the two together with different waveshapes!!!
Pbind(\instrument, \waveShapeLPFPerc, \wave, 2790, \dur, 2, \midinote, Pseq([60,67,68],inf),
	\p1, 10, \p2, 1, \p3, 10, \p4, 5, \t1, 0.4, \t2, 0.4, \t3, 1.2, \vibr, Pseq([13,5,1,0],inf)).trace.play
);
(
Pbind(\instrument, \waveShapeHPFPerc, \wave, 2290, \dur, 2, \midinote, Pseq([60,67,68],inf),
	\p1, 10, \p2, 1, \p3, 10, \p4, 5, \t1, 0.4, \t2, 0.4, \t3, 1.2, \vibr, Pseq([13,5,1,0],inf)).trace.play
);*/



// waveshapes con chorus
SynthDef(\waveShapeCPerc, {
	| wave=1, freq=300, cents=0, beats=0.5, amp=1, att=0.001, rel=1.5, gate=1, out=0 |
	var sig, env;
	sig= COsc.ar(wave, freq*(cents*0.01).midiratio, beats, amp);
	env= EnvGen.kr(
		Env.perc(att,rel),
		gate, doneAction:2);
	Out.ar(out, sig*env)
}).add;

//Pbind(\instrument, \waveShapeCPerc, \dur, 2, \midinote, Pseq([60, 67, 68],inf)).trace.play


// waveshapes que pueden interpolar entre unas y otras
SynthDef(\waveShapeVPerc, {
	|varTime=15, fase=1, wMin=0, wMax= 100, freq=300, cents=0, amp=1, att=0.01, rel=1.5, gate=1, out=0|
	var vars, osc, sig, env;
	vars= ~variable.(wMin, wMax);
	osc= SinOsc.ar(varTime, fase, vars.mul, vars.addi);
	sig= VOsc.ar(osc, freq*(cents*0.01).midiratio, 0, amp);
	env= EnvGen.kr(
		Env.perc(att,rel),
		gate, doneAction:2);
	Out.ar(out, sig*env)
}).add;

//Pbind(\instrument, \waveShapeVPerc, \dur, 0.2, \varTime, 0.0005, \fase, Pseq([0.25pi,0.75pi,1.25pi,1.75pi],inf), \wMin, 0, \wMax, 2000, \midinote, Pseq([60, 67, 68],inf)).trace.play

SynthDef(\waveShapeV3Perc, {
	| varTime=15, fase=1, wMin=0, wMax= 100, freq=300, cents=0, offset=#[0,0,0], amp=1, att=0.01, rel=1.5, gate=1, out=0 |
	var vars, osc, pitch, sig, env;
	pitch= freq*(cents*0.01).midiratio;
	vars= ~variable.(wMin, wMax);
	osc= SinOsc.ar(varTime, fase, vars.mul, vars.addi);
	sig= VOsc3.ar(osc, pitch+offset[0], pitch+offset[1], pitch+offset[2], amp);
	env= EnvGen.kr(
		Env.perc(att,rel),
		gate, doneAction:2);
	Out.ar(out, sig*env)
}).add;




// ----------------------------------------; // FX and Filters

SynthDef(\compander, {|in=10, thresh=0.5, control=0.9, pan=0, out=0, amp=1, gate=1 |
	var sig, env;

	sig= Compander.ar(Pan2.ar(In.ar(in),pan).distort,WhiteNoise.ar(control),thresh,mul:0.99)*amp;
	env= EnvGen.kr(Env.adsr(0.01,0.01, 1, 0.5, 1),gate, doneAction:2);

	Out.ar(out, sig*env)

}).add;






