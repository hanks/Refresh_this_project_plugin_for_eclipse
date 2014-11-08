Refresh this project
===========================

Detect opened file in editor or selected file in package explorer to find project root, and refresh the whole project.

**Use github to host my refresh-this-project plugin for eclipse marketplace** 

## Why
Everyday I need to git checkout different branches many times during a job, and I used some git plugins for eclipse before, once I checkout to other branches, the plugin will refresh the project automatically, It is easy to use, but the project is too big, especially too many binary files seem to slow git task. 

So I removed these git plugins and it became kind of faster in the UI operations, but every time I checkout to other branches, I have to refresh the whole project by hand. Firstly to find the root of the project, click its name and press F5 to refresh the project. According to the **Do not Repeat Youself** rule, I create this simple plugin.

## Demo
![alt text][demo]

[demo]: 
https://raw.githubusercontent.com/hanks/Refresh_this_project_plugin_for_eclipse/master/demo/demo.gif "demo"

##Usage
[button]: 
https://raw.githubusercontent.com/hanks/Refresh_this_project_plugin_for_eclipse/master/icons/sample.gif "button"

1. Just click the button ![alt text][button] in the toolbar
2. Shortcut: Shift+Ctrl+Z

**shortcut can not be changed now, sorry for this...**

##Implementation
<ol>
  <li>Job</li>
    <ol>
      <li>Because refresh the whole project is a heavy job to main UI thread of eclipse, sometimes it will block user input, and this is not a good user experience. Use <a href="http://www.vogella.com/tutorials/EclipseJobs/article.html">Job</a> can solve this problem, it can do asynchronous work from other thread, and update UI during the heavy task.</li> 
    </ol>
  <li>Shortcut     
    <ol>
      <li>It is easy to add a key shortcut for a Command, so first bind your action to a command, and create a keybind to the command. It is done. You can see more in <a href="http://eclipseo.blogspot.jp/2008/03/world-at-ur-finger-tips-key-binding.html">here</a>.</li>
  </li>
    </ol>
  </li>                
</ol>

## Install
There are two ways to install this plugin.

<ol>
  <li>From local</li>
    <ol>
      <li>Download and *.jar file in release folder to your eclipse plugin folder, and restart eclipse. It will be shown in the toolbar</li>
    </ol>
  <li>From update site      
    <ol>
      <li>Select Help->Install New Software...</li>
      <li>Input <a>https://raw2.github.com/hanks/Refresh_this_project_plugin_for_eclipse/master/release/update_site/</a> and press Add to install.
  </li>
    </ol>
  </li>                
</ol>

## Bugs
1. can not support selected folder or non program language files in the package explorer, like *.txt, *.png, but it is fine when files are opened in the editor.

## Contribution
**Waiting for your pull request**

## Lisence
MIT Lisence
