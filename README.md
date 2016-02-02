

## DIagrams Through Ascii Art

(((-intro-))) (((-download-))) (((-usage and syntax-))) (((-friends-))) (((-contact-)))

ditaa is a small command-line utility written in Java, that can convert diagrams drawn using ascii art ('drawings' that contain characters that resemble lines like | / - ), into proper bitmap graphics. This is best illustrated by the following example -- which also illustrates the benefits of using ditaa in comparison to other methods :)

    +--------+   +-------+    +-------+
    |        | --+ ditaa +--> |       |
    |  Text  |   +-------+    |diagram|
    |Document|   |!magic!|    |       |
    |     {d}|   |       |    |       |
    +---+----+   +-------+    +-------+
        :                         ^
        |       Lots of work      |
        +-------------------------+
After conversion using ditaa, the above file becomes:
round
		corner demo
ditaa interprets ascci art as a series of open and closed shapes, but it also uses special markup syntax to increase the possibilities of shapes and symbols that can be rendered.

ditaa is open source and free software (free as in free speech), since it is released under the GPL license.

### BUT WHY? Does this thing have any real use?

There are several reasons why I did this:

Simply for hack value. I wanted to know if/how it could be done and how easily.
Aesthetic reasons and legacy formats: there are several old FAQs with ascii diagrams lying out there. At this time and age ascii diagrams make my eyes hurt due to their ugliness. ditaa can be used to convert them to something nicer. Although ditaa would not be able to convert all of them (due to differences in drawing 'style' in each case), it could prove useful in the effort of modernising some of those documents without too much effort. I also know a lot of people that can make an ascii diagram easily, but when it gets to using a diagram program, they don't do very well. Maybe this utility could help them make good-looking diagrams easily/quickly.
Embedding diagrams to text-only formats: There is a number of formats that are text-based (html, docbook, LaTeX, programming language comments), but when rendered by other software (browsers, interpreters, the javadoc tool etc), they can contain images as part of their content. If ditaa was intergrated with those tools (and I'm planning to do the javadoc bit myself soon), then you would have readable/editable diagrams within the text format itself, something that would make things much easier. ditaa syntax can currently be embedded to HTML.
Reusability of "code": Suppose you make a diagram in ascii art and you render it with version 0.6b of ditaa. You keep the ascii diagram, and then version 0.8 comes out, which features some new cool effects. You re-render your old diagram with the new version of ditaa, and it looks better, with zero effort! In that sense ditaa is a diagram markup language, with very loose syntax.

## Download

The latest version of ditaa can be obtained from its SourceForge project page.

You can checkout the code using:

   svn co https://ditaa.svn.sourceforge.net/svnroot/ditaa ditaa

You can also browse the code online.

## Usage and syntax

### Command line

You need the latest Java runtimes (JRE) to use ditaa. The best
anti-aliasing can be achieved using Java 1.5 or higher.

To start from the command line, type (where `XXX` is the version number):

`java -jar ditaaXXX.jar`

You will be presented with the command-line options help:

```
 -A,--no-antialias          Turns anti-aliasing off.
 -d,--debug                 Renders the debug grid over the resulting
                            image.
 -E,--no-separation         Prevents the separation of common edges of
                            shapes. You can see the difference below:
```

```
+---------+
| cBLU    |
|         |
|    +----+
|    |cPNK|
|    |    |
+----+----+
```

Before processing	Common edge
separation (default)	No separation
(with the -E option)

```
-e,--encoding <ENCODING>   The encoding of the input file.
 -h,--html                  In this case the input is an HTML file. The
                            contents of the <pre class="textdiagram"> tags
                            are rendered as diagrams and saved in the
                            images directory and a new HTML file is
                            produced with the appropriate <img> tags.
                            See the HTML section.
    --help                  Prints usage help.
 -o,--overwrite             If the filename of the destination image
                            already exists, an alternative name is chosen.
                            If the overwrite option is selected, the image
                            file is instead overwriten.
 -r,--round-corners         Causes all corners to be rendered as round
                            corners.
 -s,--scale <SCALE>         A natural number that determines the size of
                            the rendered image. The units are fractions of
                            the default size (2.5 renders 1.5 times bigger
                            than the default).
 -S,--no-shadows            Turns off the drop-shadow effect.
 -t,--tabs <TABS>           Tabs are normally interpreted as 8 spaces but
                            it is possible to change that using this
                            option. It is not advisable to use tabs in
                            your diagrams.
 -v,--verbose               Makes ditaa more verbose.
```

### Syntax

#### Round corners

If you use `/` and `\` to connect corners, they are rendered as round corners:

```
/--+
|  |
+--/
```

round corner demo
Before processing	Rendered

#### Color

Color codes can be used to add color to the diagrams. The syntax of
color codes is

`cXXX`

where XXX is a hex number. The first digit of the number represents
the red compoment of the color, the second digit represents green and
the third blue (good ol' RGB). See below for an example of use of
color codes:

```
/----\ /----\
|c33F| |cC02|
|    | |    |
\----/ \----/

/----\ /----\
|c1FF| |c1AB|
|    | |    |
\----/ \----/
```

color demo
Before processing	Rendered

This can become a bit tedious after a while, so there are (only some
for now) human readable color codes provided:

Color codes

```
/-------------+-------------\
|cRED RED     |cBLU BLU     |
+-------------+-------------+
|cGRE GRE     |cPNK PNK     |
+-------------+-------------+
|cBLK BLK     |cYEL YEL     |
\-------------+-------------/
```

color code
Before processing	Rendered

As you can see above, if a colored shape contains any text, the color
of the text is adjusted according to the underlying color. If the
undelying color is dark, the text color is changed to white (from the
default black).

Note that color codes only apply if they are within closed shapes, and
they have no effect anywhere outside.

#### Tags

ditaa recognises some tags that change the way a rectangular shape is rendered. All tags are between `{` and `}`. See the table below:

Name	Original	Rendered	Comment

Document
```
+-----+
|{d}  |
|     |
|     |
+-----+
```
Symbol representing a document.

Storage
```
+-----+
|{s}  |
|     |
|     |
+-----+
```

Symbol representing a form of storage, like a database or a hard disk.

Input/Output
```
+-----+
|{io} |
|     |
|     |
+-----+
```
	Symbol representing input/output.

#### Dashed lines

Any lines that contain either at least one `=` (for horizontal lines)
or at least one `:` (for vertical lines) are rendered as dashed
lines. Only one of those characters can make a whole line dashed, so
this feature "spreads". The rationale behind that is that you only
have to change one character to switch from normal to dashed (and vice
versa), rather than redrawing the whole line/shape. Special symbols
(like document or storage symbols) can also be dashed. See below:

```
----+  /----\  +----+
    :  |    |  :    |
    |  |    |  |{s} |
    v  \-=--+  +----+
```
Before processing	Rendered

#### Point markers

If `*` is encountered on a line (but not at the end of the line), it
is rendered as a special marker, called the point marker (this feature
is still experimental). See below:

```
*----*
|    |      /--*
*    *      |
|    |  -*--+
*----*
```
point marker demo
Before processing	Rendered

#### Text handling

If the pattern ' o XXXXX' is encountered, where XXXXX is any text, the
'o' is interpreted and rendered as a bullet point. Note that there
must be a space before the 'o' as well as after it. See below:

```
/-----------------\
| Things to do    |
| cGRE            |
| o Cut the grass |
| o Buy jam       |
| o Fix car       |
| o Make website  |
\-----------------/
```
bullet point demo
Before processing	Rendered

#### HTML mode

When ditaa is run using the `--html` option, the input is an HTML
file. The contents of the `<pre class="textdiagram">` tags are rendered
as diagrams and saved in the images directory and a new HTML file is
produced with the appropriate `<img>` tags.

If the id parameter is present in the `<pre>` tag, its value is used as
the filename of the rendered png. Otherwise a filename of the form
`ditaa_diagram_X.png` is used, where X is a number. Similarly, if there
is no output filename specified, the converted html file is named in
the form of `xxxx_processed.html`, where `xxxx` is the filename of the
original file.

In this mode, files that exist are not generated again, they are just
skipped. You can force overwrite of the files using the `--overwrite`
option.

## Friends and relatives

Here is a list of projects that are related to ditaa and add to its functionality:

* Mikael Brännström's
  [ditaa-addons](http://ditaa-addons.sourceforge.net/) offers two very
  interesting pieces of functionality: The Textdiagram Javadoc taglet
  allows you to embedd ascii diagrams to Java comments and have them
  rendered as ditaa diagrams in the HTML output. The Ditaa Eps program
  allows you to render to EPS instead of PNG.
* Mathieu Lecarme's [ditaa-web](http://ditaa.sourceforge.net/) allows
  you to install ditaa on your web server and use it as a webservice
  through a HTTP POST request.
* ditaa comes bundled within [org-mode](http://orgmode.org/), a major
  mode for emacs. Here's an example of how you can use it.
* There is a plugin for DocuWiki that allows you to render ditaa
  diagrams in the wiki pages. Written by Dennis Ploeger.

Here's a list of projects that are somehow related to ditaa, and could prove useful/relevant while using it:

* Nadim Khemir's asciio could be used for producing the ascii diagrams
  more easily. It would require some modifications to its default
  settings for the diagrams to render properly (specifically the
  characters used for corners should all be set to +). Here is a
  screencast of how asciio works.
* Markus Gebhard's JavE could be used for the same purpose, although
  I've never tried it.
* If you make sure to keep the lines straight, artist-mode for emacs
  can be used to produce ditaa-compatible diagrams.

## Contributors

* Stathis Sideris - original author
* Bill Baker - co-maintainer
* John Tsiombikas - beta testing
* Leonidas Tsampros - beta testing

Thanks to Steve Purcell for writing JArgs.

Thanks to Mr. Jericho for writing Jericho HTML Parser.
