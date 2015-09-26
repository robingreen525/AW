import threading
import shutil
import os
import time
from time import sleep
from flask import request, render_template, redirect, url_for, flash, Markup
from app import app
from form import Form, SubmissionForm
from hsprunner import Hsprunner
from subprocess import Popen, PIPE, STDOUT

@app.route('/', methods=['POST','GET'])
def process_form():
    form = SubmissionForm(request.form)
    error = None
    if request.method == 'POST':
        if request.form['submit'] == 'submit' and form.validate():
            hsp = Hsprunner(form)
            hsp_thread = threading.Thread(target=hsp.run)
            hsp_thread.start()
            q_string = ('?outlog=' + hsp.paths.outlog + '&outlog_final=' + 
                        hsp.paths.outlog_final)
            return redirect(url_for('running')+ q_string)
        elif request.form['submit'] == 'clear':
            form = SubmissionForm()
    return render_template('hspipeline.html', form=form)

@app.route('/running')
def running():
    time.sleep(0.5)
    outlog_location = request.args['outlog']
    outlog_final_location = request.args['outlog_final']
    finished = os.path.exists(outlog_final_location)
    outlog_path = finished and outlog_final_location or outlog_location

    outlog = None
    try:
        outlog = open(outlog_path, 'r').read()
    except IOException as e:
        outlog.close()
        print 'call failed {0}: {1}'.format(e.errno, e.strerror)

    if finished:
        return render_template('finished.html', outlog=outlog)
    else:
        return render_template('running.html', outlog=outlog)
