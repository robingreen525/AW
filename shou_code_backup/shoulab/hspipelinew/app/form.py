import os,sys, re
from wtforms import validators, ValidationError
from wtforms import Form, TextField, BooleanField, SubmitField, HiddenField, TextAreaField, IntegerField
from hsprunner import Paths

class PathExists(object):        

    def __init__(self, prefix=Paths.data_web, message=None):
        self.prefix  = prefix
        self.message = message
        self.path    = None

        self.path_regex = r'^[\\/\w\-.]+$'


    def __call__(self, form, field):
        self.path = os.path.join(self.prefix, field.data)

        if re.match(self.path_regex, self.path) is None:
            self.message = (u'Folder %s contains disallowed characters.'
                            % (self.path))
            raise ValidationError(self.message)

        if not os.path.isdir(self.path):
            self.message = u'Folder %s does not exist.' % (self.path)
            raise ValidationError(self.message)

class FileExists(PathExists):
    def __call__(self, form, field):
        self.path = os.path.join(self.prefix,field.data)

        if re.match(self.path_regex, self.path) is None:
            self.message = (u'File %s contains disallowed characters.'
                            % (self.path))
            raise ValidationError(self.message)

        if not os.path.exists(self.path):
            self.message = u'File %s does not exist.' % (self.path)
            raise ValidationError(self.message)

class SubmissionForm(Form):
    project_path = TextField('project folder',
            [validators.Required(message='Must include project path'),
             PathExists()])
    anc_path = TextField('ancestor folder',
            [validators.Required(message='Must include ancestor path'),
             PathExists()])
    ref_path = TextField('reference sequence file',
            [validators.Required(message='Must include reference path'),
             FileExists()])

    align = BooleanField('align', default='y')
    trim = BooleanField('trim', default='')
    find = BooleanField('find', default='y')
    compare = BooleanField('compare', default='y')
    plot = BooleanField('plot', default='y')
