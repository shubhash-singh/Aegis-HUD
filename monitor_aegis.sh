
#!/usr/bin/env bash

PACKAGE="dev.ragnar.aegis"
DURATION=30
INTERVAL=1

# Verify ADB connection
if ! adb get-state >/dev/null 2>&1; then
    echo "Error: No Android device connected via ADB."
    exit 1
fi

# Find app PID
PID=$(adb shell pidof "$PACKAGE" | tr -d '\r')

if [[ -z "$PID" ]]; then
    echo "Error: $PACKAGE is not running."
    exit 1
fi

echo "=========================================="
echo "       AEGIS PERFORMANCE MONITOR"
echo "=========================================="
echo "Package   : $PACKAGE"
echo "PID       : $PID"
echo "Duration  : ${DURATION}s"
echo "Interval  : ${INTERVAL}s"
echo "=========================================="
echo ""

declare -a RAM_VALUES
declare -a CPU_VALUES

for ((i=1; i<=DURATION; i++)); do

    # RAM consumption in KB (TOTAL PSS)
    RAM=$(adb shell dumpsys meminfo "$PID" |
        awk '/TOTAL PSS:/ {print $3; exit}')

    # CPU usage percentage for the process
    CPU=$(adb shell top -b -n 1 -p "$PID" |
        awk -v pid="$PID" '
            NR > 5 && $1 == pid {
                for (i=1; i<=NF; i++) {
                    if ($i ~ /^[0-9.]+%?$/ && i == 9) {
                        gsub(/%/, "", $i)
                        print $i
                        exit
                    }
                }
            }')

    RAM=${RAM:-0}
    CPU=${CPU:-0}

    RAM_VALUES+=("$RAM")
    CPU_VALUES+=("$CPU")

    printf "[%02d/%02d] RAM: %8s MB | CPU: %6s%%\n" \
        "$i" "$DURATION" \
        "$(awk "BEGIN {printf \"%.2f\", $RAM/1024}")" \
        "$CPU"

    sleep "$INTERVAL"
done

# Calculate statistics
calculate_stats() {
    printf '%s\n' "$@" | awk '
    BEGIN {min=999999; max=0; sum=0; count=0}
    {
        value=$1
        sum+=value
        if(value<min) min=value
        if(value>max) max=value
        count++
    }
    END {
        if(count>0)
            printf "Average: %.2f | Minimum: %.2f | Maximum: %.2f\n",
                sum/count, min, max
    }'
}

echo ""
echo "=========================================="
echo "           PERFORMANCE REPORT"
echo "=========================================="

echo -n "RAM (MB)  : "
RAM_MB=()
for value in "${RAM_VALUES[@]}"; do
    RAM_MB+=("$(awk "BEGIN {printf \"%.2f\", $value/1024}")")
done
calculate_stats "${RAM_MB[@]}"

echo -n "CPU (%)   : "
calculate_stats "${CPU_VALUES[@]}"

echo "Samples   : ${#RAM_VALUES[@]}"
echo "Duration  : ${DURATION} seconds"
echo "=========================================="

