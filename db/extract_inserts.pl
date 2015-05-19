#!/usr/bin/env perl

use strict;
use warnings;

use v5.16;

use File::Slurp::Tiny qw(read_file);

my $file_name = shift || "test-data-split.sql";

my $sql = read_file($file_name) || die "Can't read $file_name";

my @insert_strings = grep(/INSERT/,split("--", $sql ));

say join("\n--\n",@insert_strings);
