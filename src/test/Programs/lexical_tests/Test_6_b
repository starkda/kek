routine Main() is
    var i : integer

    var j : integer

    var x : integer is 1000

    var y : integer is 0


    for i in 1 .. 1000 loop
        x = x - 1
    end

    if x = 0
        then x = 5
        else x = 0
    end

    for i in reverse 1 .. 1000 loop
        x = x + i - (i / 2)
    end
    x = 0
    for i in 1 .. 1000 loop
        for j in reverse 1 .. 1000 loop
            x = i + j
        end
        x = x + i - (i / 2)
    end
end