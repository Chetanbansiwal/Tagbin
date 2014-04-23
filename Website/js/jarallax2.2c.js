/*!
 * do not redistribute 
 * jaralax library
 * version: 0.2.2c patched version
 * http://jarallax.com/
 *
 * Copyright 2012, Jacko Hoogeveen
 * Dual licensed under the MIT or GPL Version 3 licenses.
 * http://jarallax.com/license.html
 * 
 * Date: 6/26/2012
 */

function hasNumbers(e){return/\d/.test(e)}var Jarallax=function(e){this.jarallaxObject=[],this.animations=[],this.defaultValues=[],this.progress=0,this.controllers=[],this.maxProgress=1,this.targetProgress=0,this.timer,this.allowWeakProgress=!0;if(e===undefined)this.controllers.push(new ControllerScroll);else if(e.length)this.controllers=e;else{if(typeof e!="object")throw new Error('wrong controller data type: "'+typeof e+'". Expected "object" or "array"');this.controllers.push(e)}for(var t=0;t<this.controllers.length;t++)this.controllers[t].activate(this)};Jarallax.prototype.setProgress=function(e,t){t|=!1,e>1?e=1:e<0&&(e=0),this.progress=e;if(this.allowWeakProgress||!t){for(j=0;j<this.defaultValues.length;j++)this.defaultValues[j].activate(this.progress);for(k=0;k<this.animations.length;k++)this.animations[k].activate(this.progress);for(l=0;l<this.controllers.length;l++)this.controllers[l].update(this.progress)}},Jarallax.prototype.jumpToProgress=function(e,t,n){return e.indexOf?e.indexOf("%")!=-1&&(e=parseFloat(e)/100):e/=this.maxProgress,e>1?e=1:e<0&&(e=0),this.smoothProperties={},this.smoothProperties.timeStep=1e3/n,this.smoothProperties.steps=t/this.smoothProperties.timeStep,this.smoothProperties.currentStep=0,this.smoothProperties.startProgress=this.progress,this.smoothProperties.diffProgress=e-this.progress,this.smoothProperties.previousValue=this.progress,this.smooth(),this.allowWeakProgress=!1,!1},Jarallax.prototype.smooth=function(e){if(!e)var e=this;else var e=e;e.smoothProperties.currentStep++,clearTimeout(e.timer);if(e.smoothProperties.currentStep<e.smoothProperties.steps){var t=e.smoothProperties.currentStep/e.smoothProperties.steps,n=Jarallax.EASING.easeOut(t,e.smoothProperties.startProgress,e.smoothProperties.diffProgress,1,5);e.setProgress(n);var r=e.smoothProperties.timeStep,i=e.smooth;e.timer=window.setTimeout(function(){e.smooth(e)},10),e.smoothProperties.previousValue=n,e.allowWeakProgress=!1}else e.allowWeakProgress=!0,e.setProgress(e.smoothProperties.startProgress+e.smoothProperties.diffProgress),delete e.smoothProperties},Jarallax.prototype.setDefault=function(e,t){if(!e)throw new Error("no selector defined.");if(Jarallax.isValues(t)){var n=new JarallaxDefault(e,t);n.activate(),this.defaultValues.push(n)}},Jarallax.prototype.addStatic=function(e,t){if(!e)throw new Error("no selector defined.");if(Jarallax.isValues(t)){var n=new JarallaxStatic(e,t[0],t[1]);this.defaultValues.push(n)}},Jarallax.prototype.addAnimation=function(e,t){if(!e)throw new Error("no selector defined.");var n=[];if(Jarallax.isValues(t))for(var r=0;r<t.length-1;r++){if(!t[r]||!t[r+1])throw new Error("bad animation data.");if(!t[r].progress||!t[r+1].progress)throw new Error("no animation boundry found.");t[r+1]["progress"].indexOf("%")==-1&&this.maxProgress<t[r+1].progress&&(this.maxProgress=t[r+1].progress);var i=new JarallaxAnimation(e,t[r],t[r+1],this);this.animations.push(i),n.push(i)}return n},Jarallax.prototype.cloneAnimation=function(e,t,n){if(!e)throw new Error("no selector defined.");var r=[],i=[];for(var s=0;s<n.length+1;s++)t instanceof Array?i.push(t[s]):i.push(t);for(s=0;s<n.length;s++){var o=n[s],u=Jarallax.clone(o.startValues),a=Jarallax.clone(o.endValues),f=i[s],l=i[s+1];for(var c in u)f[c]&&(u[c]=Jarallax.calculateNewValue(f[c],u[c]));for(var h in a)l[h]&&(a[h]=Jarallax.calculateNewValue(l[h],a[h]));r.push(this.addAnimation(e,[u,a])[0])}return r},Jarallax.calculateNewValue=function(e,t){var n;return e.indexOf("+")==0?n=String(parseFloat(t)+parseFloat(e)):e.indexOf("-")==0?n=String(parseFloat(t)+parseFloat(e)):e.indexOf("*")==0?n=String(parseFloat(t)*parseFloat(e)):e.indexOf("/")==0?n=String(parseFloat(t)/parseFloat(e)):n=e,t.indexOf&&t.indexOf("%")>0?n+"%":n},Jarallax.isValues=function(e){if(!e)throw new Error("no values set.");if(typeof e!="object")throw new Error('wrong data type values. expected: "object", got: "'+typeof e+'"');if(e.size===0)throw new Error("Got an empty values object");return!0},Jarallax.getUnits=function(e){return e.replace(/\d+/g,"")},Jarallax.clone=function(e){var t={};for(var n in e)t[n]=e[n];return t},Jarallax.EASING={linear:function(e,t,n,r,i){return e/r*n+t},easeOut:function(e,t,n,r,i){return i==undefined&&(i=2),(Math.pow((r-e)/r,i)*-1+1)*n+t},easeIn:function(e,t,n,r,i){return i==undefined&&(i=2),Math.pow(e/r,i)*n+t},easeInOut:function(e,t,n,r,i){return i==undefined&&(i=2),n/=2,e*=2,e<r?Math.pow(e/r,i)*n+t:(e-=r,(Math.pow((r-e)/r,i)*-1+1)*n+t+n)}},Jarallax.EASING.none=Jarallax.EASING.linear,JarallaxAnimation=function(e,t,n,r){this.progress=0,this.selector=e,this.startValues=t,this.endValues=n,this.jarallax=r},JarallaxAnimation.prototype.activate=function(e){if(this.progress!=e){var t,n,r;this.startValues["style"]==undefined?r={easing:"linear"}:r=this.startValues.style,this.startValues.progress.indexOf("%")>=0?t=parseInt(this.startValues.progress,10)/100:hasNumbers(this.startValues.progress)&&(t=parseInt(this.startValues.progress,10)/this.jarallax.maxProgress),this.endValues.progress.indexOf("%")>=0?n=parseInt(this.endValues.progress,10)/100:hasNumbers(this.endValues.progress)&&(n=parseInt(this.endValues.progress,10)/this.jarallax.maxProgress),this.startValues.event&&this.dispatchEvent(this.progress,e,t,n);if(e>=t&&e<=n)for(i in this.startValues)if(i!="progress"&&i!="style"&&i!="event")if(undefined!=this.endValues[i]&&i!="display"){var s=Jarallax.getUnits(this.startValues[i]+"");s=s.replace("-","");var o=parseFloat(this.startValues[i]),u=parseFloat(this.endValues[i]),a=n-t,f=e-t,l=u-o,c=Jarallax.EASING[r.easing](f,o,l,a,r.power);c+=s,$(this.selector).css(i,c)}else $(this.selector).css(i,this.startValues[i]);this.progress=e}},JarallaxAnimation.prototype.dispatchEvent=function(e,t,n,r){var i=this.startValues.event,s={};s.animation=this,s.selector=this.selector,t>=n&&t<=r?(i.start&&e<n&&(s.type="start",i.start(s)),i.start&&e>r&&(s.type="rewind",i.start(s)),i.animating&&(s.type="animating",i.animating(s)),i.forward&&e<t&&(s.type="forward",i.forward(s)),i.reverse&&e>t&&(s.type="reverse",i.reverse(s))):(i.complete&&e<r&&t>r&&(s.type="complete",i.complete(s)),i.rewinded&&e>n&&t<n&&(s.type="rewind",i.rewinded(s)))},JarallaxDefault=function(e,t){this.selector=e,this.values=t},JarallaxDefault.prototype.activate=function(e){for(i in this.values)$(this.selector).css(i,this.values[i])},JarallaxStatic=function(e,t,n){this.selector=e,this.values=values},JarallaxStatic.prototype.activate=function(e){var t,n;this.startValues.progress.indexOf("%")>=0?t=parseInt(this.startValues.progress,10)/100:hasNumbers(this.startValues.progress)&&(t=this.maxProgress/parseInt(this.startValues.progress,10)),this.endValues.progress.indexOf("%")>=0?n=parseInt(this.endValues.progress,10)/100:hasNumbers(this.endValues.progress)&&(n=this.maxProgress/parseInt(this.endValues.progress,10));if(progress>t&&progress<n)for(i in this.startValues)i!="progress"&&$(this.selector).css(i,this.startValues[i])},ControllerScroll=function(e){this.height=parseInt($("body").css("height"),10),this.target=$(window),this.scrollSpace=this.height-this.target.height(),this.smoothing=e|!1,this.timer,this.targetProgress=0},ControllerScroll.prototype.activate=function(e){this.jarallax=e,this.target.bind("scroll",{me:this},this.onScroll)},ControllerScroll.prototype.deactivate=function(e){},ControllerScroll.prototype.onScroll=function(e){var t=e.data.me,n=t.target.scrollTop(),r=n/t.scrollSpace;t.smoothing?(t.targetProgress=r,t.smooth()):t.jarallax.setProgress(r,!0)},ControllerScroll.prototype.smooth=function(e){if(!e)var e=this;var t=e.jarallax.progress,n=e.targetProgress-t;clearTimeout(e.timer);if(n>1e-4||n<-0.0001){var r=t+n/5;e.timer=window.setTimeout(function(){e.smooth(e)},30),e.jarallax.setProgress(r,!0)}else e.jarallax.setProgress(e.targetProgress,!0)},ControllerScroll.prototype.update=function(e){var t=e*this.scrollSpace;this.jarallax.allowWeakProgress||($("html").scrollTop(t),$("body").scrollTop(t))},ControllerTime=function(e,t){this.interval=t,this.speed=e,this.forward=!0},ControllerTime.prototype.onInterval=function(){this.jarallax.setProgress(this.progress),$("body").scrollTop(parseInt(jQuery("body").css("height"),10)*this.progress),this.progress>=1?(this.progress=1,this.forward=!1):this.progress<=0&&(this.progress=0,this.forward=!0),this.forward?this.progress+=this.speed:this.progress-=this.speed},ControllerTime.prototype.activate=function(e){this.jarallax=e,this.progress=0,this.interval=$.interval(this.onInterval.bind(this),this.interval)},ControllerTime.prototype.deactivate=function(e){},ControllerTime.prototype.update=function(e){},ControllerDrag=function(e,t,n){this.object=$(e),this.start=t,this.end=n,this.container="",this.width,this.startX=0,this.startY=0},ControllerDrag.prototype.activate=function(e){this.jarallax=e,this.container="#scrollbar",this.object.draggable({containment:this.container,axis:"x"}),this.object.bind("drag",{me:this},this.onDrag),this.container=$(this.container),this.width=$(this.container).innerWidth()-this.object.outerWidth()},ControllerDrag.prototype.onDrag=function(e){var t=parseInt($(this).css("left"),10),n=t/e.data.me.width;e.data.me.jarallax.setProgress(n)},ControllerDrag.prototype.deactivate=function(e){},ControllerDrag.prototype.update=function(e){this.object.css("left",e*this.width)},ControllerKeyboard=function(e,t,n){this.repetitiveInput=n,this.preventDefault=t||!1,this.keys=e||{38:-0.01,40:.01},this.keysState=new Object},ControllerKeyboard.prototype.activate=function(e){this.jarallax=e,$(document.documentElement).keydown({me:this},this.keyDown),$(document.documentElement).keyup({me:this},this.keyUp);for(key in this.keys)this.keysState[key]=!1},ControllerKeyboard.prototype.deactivate=function(e){},ControllerKeyboard.prototype.keyDown=function(e){var t=e.data.me;for(key in t.keys)key==e.keyCode&&((t.keysState[key]!==!0||t.repetitiveInput)&&t.jarallax.setProgress(t.jarallax.progress+t.keys[key]),t.keysState[key]=!0,t.preventDefault&&e.preventDefault())},ControllerKeyboard.prototype.keyUp=function(e){var t=e.data.me;for(key in t.keys)key==e.keyCode&&(t.keysState[key]=!1)},ControllerKeyboard.prototype.update=function(e){},ControllerMousewheel=function(e,t){this.sensitivity=-e,this.preventDefault=t||!1},ControllerMousewheel.prototype.activate=function(e){this.jarallax=e,$("body").bind("mousewheel",{me:this},this.onScroll)},ControllerMousewheel.prototype.deactivate=function(e){this.jarallax=e},ControllerMousewheel.prototype.onScroll=function(e,t){controller=e.data.me,controller.jarallax.setProgress(controller.jarallax.progress+controller.sensitivity*t),controller.preventDefault&&e.preventDefault()},ControllerMousewheel.prototype.update=function(e){},ControllerIpadScroll=function(){this.x=0,this.previousX=-1,this.top=700,this.moveRight=!1},ControllerIpadScroll.prototype.activate=function(e,t){this.jarallax=e,this.values=t,$("body").bind("touchmove",{me:this},this.onScroll)},ControllerIpadScroll.prototype.onScroll=function(e){e.preventDefault();var t=e.data.me,n=e.originalEvent.touches.item(0);t.previousX==-1?t.previousX=n.clientX:(n.clientX-t.previousX<100&&n.clientX-t.previousX>-100&&(t.moveRight?t.x-=n.clientX-t.previousX:t.x+=n.clientX-t.previousX,t.x=t.x<1e3?t.x:1e3,t.x=t.x>0?t.x:0),t.previousX=n.clientX,t.jarallax.setProgress(t.x/t.top))},ControllerIpadScroll.prototype.deactivate=function(e){},ControllerIpadScroll.prototype.update=function(e){}
