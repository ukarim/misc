require "date"
require "erb"
require "set"

# Utils

def pair(a, b)
  [a, b]
end

month_names = {
  1 => "январь",
  2 => "февраль",
  3 => "март",
  4 => "апрель",
  5 => "май",
  6 => "июнь",
  7 => "июль",
  8 => "август",
  9 => "сентябрь",
  10 => "октябрь",
  11 => "ноябрь",
  12 => "декабрь"
}

holidays = Set[
    "01-01",
    "02-01",
    "08-03",
    "21-03",
    "22-03",
    "23-03",
    "01-05",
    "07-05",
    "09-05",
    "06-07",
    "30-08",
    "16-12"
]

# Script

year_arg = ARGV[0]
month_arg = ARGV[1]
background_img = ARGV[2]

if year_arg.nil?
  puts "year argument not provided"
  exit 1
end

if month_arg.nil?
  puts "month argument not provided"
  exit 1
end

if background_img.nil?
  puts "background image argument not provided"
  exit 1
end

year = year_arg.to_i
month = month_arg.to_i

first_day_of_month = Date.new(year, month, 1)
last_day_of_month = Date.new(year, month, (first_day_of_month.next_month().prev_day().day))

start_date = first_day_of_month.prev_day((first_day_of_month.wday + 6) % 7)
end_date = last_day_of_month.next_day(6 - ((last_day_of_month.wday + 6) % 7))

days = start_date.step(end_date).collect { |date|
  style = ""
  if month != date.month
    style = "gray"
  end
  if holidays.include?(date.strftime("%d-%m"))
    style = "holiday"
  end
  pair(date.day, style)
}

tmpl = IO.read("calendar.erb")

tmpl_params = {
    :month => month_names[month],
    :year => year,
    :days => days,
    :background => background_img
}

erb = ERB.new(tmpl)
puts erb.result(binding)
