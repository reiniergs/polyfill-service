'use strict';

const path = require('path');
const gaze = require('gaze');
const child_process = require('child_process');
const spawn = child_process.spawn;
const exec = child_process.execSync;
const argv = require('minimist')(process.argv.slice(2));

const cwd = path.join(__dirname, '../../');
const spawnOptions = { stdio: 'pipe', cwd: cwd };
const gazeOptions = { cwd: cwd };
const availableTasks = {
    "service": { cmd: "bin/polyfill-service", waitForToken: "[procstatus:started]" },
    "service-with-watch": {
        cmd: "bin/polyfill-service",
        waitForExit: true,
        watches: [
            { patterns: ['service/**/*.js', 'lib/**/*.js', 'docs/**/*', 'bin/**/*'] },
            { patterns: ['polyfills/**/*.js', 'polyfills/**/config.json', '!polyfills/__dist/**', '!polyfills/Intl/**'], beforeRestart: () => {
                console.log('Changes to polyfill sources.  Rebuilding...');
                exec('npm run build');
                console.log('Rebuild complete');
            }}
        ]
    },
    "remote-test:quick": { cmd: "node tasks/node/remotetest --targeted", waitForExit: true },
    "remote-test:compat": { cmd: "node tasks/node/remotetest --control --all --set='full' --continueOnFail", waitForExit: true },
    "remote-test:ci": { cmd: "node tasks/node/remotetest --targeted --set='ci'", waitForExit: true }
};

const taskRunners = argv._.map(taskName => {
    return () => {
        const task = availableTasks[taskName];
        if (!task) throw new Error('spawn: Unknown task '+taskName);

        let shell;
        let args;
        let buffer = '';
        let proc;

        let exitResolver;
        const exitPromise = new Promise(resolve => {
            exitResolver = resolve;
        });

        if (process.platform === "win32") {
            args = ["/s", "/c", task.cmd.replace(/\//g, "\\")];
            shell = "cmd.exe ";
            spawnOptions.windowsVerbatimArguments = true;
        } else {
            shell = "/bin/sh";
            args = ["-c", task.cmd];
        }

        function doSpawn() {
            proc = spawn(shell, args, spawnOptions);

            proc.stdout.on('data', function(d) {
                process.stdout.write(d);
                if (task.waitForToken) {
                    buffer += d.toString();
                    if (buffer.indexOf(task.waitForToken) >= 0) {
                        console.log('spawn: Successful startup of '+taskName+' detected');
                        task.waitForToken = false;
                    }
                }
            });
            proc.stderr.on('data', d => process.stderr.write(d));
            proc.on('disconnect', () => { throw new Error ('Task "'+taskName+'" disconnected'); });
            proc.on('error', err => { throw err; });
            proc.on('exit', (code, sig) => {
                if (sig === 'SIGTERM') {
                    console.log('spawn: Restarting task "'+taskName+'"...');
                    doSpawn();
                } else if (code || sig) {
                    throw new Error('spawn: Task "'+taskName+'" exited with code='+code+', signal='+sig);
                } else {
                    exitResolver();
                }
            });
        }
        doSpawn();

        console.log('spawn: Starting task "'+taskName+'" with PID = ' + proc.pid);

        if (task.watches) {
            task.watches.forEach(watch => {
                gaze(watch.patterns, gazeOptions, (err, watcher) => {
                    const watched = watcher.watched();
                    const watchedList = Object.keys(watched).reduce((out, k) => out.concat(watched[k]), []);
                    console.log('spawn: Watching '+watchedList.length+' files for changes...');
                    watcher.on('all', (ev, changedPath) => {
                        console.log('spawn: Triggered watch with changes to '+changedPath);
                        if (watch.beforeRestart) watch.beforeRestart(changedPath);
                        proc.kill();
                    });
                });
            });
        }

        if (!task.waitForExit) {
            return waitUntil(() => !task.waitForToken);
        } else {
            return exitPromise;
        }
    };
});

function runTasksSequentially() {
    const nextTaskRunner = taskRunners.shift();
    return nextTaskRunner().then(() => taskRunners.length ? runTasksSequentially() : undefined);
}

function waitUntil(fnEndOk, checkInterval = 200) {
	return new Promise(resolve => {
        const loopId = setInterval(() => {
		    if (true === fnEndOk()) {
		    	clearInterval(loopId);
			    resolve();
		    }
	    }, checkInterval);
    });
};


runTasksSequentially()
    .then(() => {
        console.log('spawn: All tasks complete');
        process.exit();
    })
    .catch(e => {
        console.log(e);
        process.exit(1);
    })
;
