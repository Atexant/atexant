
open($fh, '< sentences_result.txt');

@values = ();

while (<$fh>) {
    next unless (/err\=(0\.\d+)/);
    push @values, $1;
}

$sum=0;

@values = sort @values;

for (@values) {$sum+=$_};

$avg = $sum/scalar(@values);

print join(",", @values);

printf("avg=%f\n", $avg);