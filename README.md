# Jumpy
Easy kotlin-friendly request helper.
It allows to load and parse a set of objects, writing only a couple of lines of code. This is very useful when specific screen, requires data loading from the multiple resources.

## Example:
        val jumpy = Jumpy()
        jumpy.add(Jumpy.ObjectRequest<Object1>("http://url1"))
        jumpy.add(Jumpy.ObjectRequest<Object2>("http://url2"))
        val loaded = jumpy.load()

        val object1 = loaded?.get("http://url1") as Object1
        val object2 = loaded?.get("http://url2") as Object2
