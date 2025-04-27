document.addEventListener("DOMContentLoaded", function () {

    //Model create & edit validation habit
    const habitForm = document.getElementById("habitForm");
    const habitFormUpdate = document.getElementById("habitUpdateForm");
    const habitTitleInput = document.getElementById("habitTitle");
    const habitTitleUpdateInput = document.getElementById("habitUpdateTitle");
    const habitTitleError = document.getElementById("habitTitleError");
    const habitTitleUpdateError = document.getElementById("habitTitleUpdateError");
    const habitTarget = document.getElementById("target")
    const habitTargetError = document.getElementById("targetError")
    const habitUpdateTarget = document.getElementById("upTarget")
    const habitUpdateTargetError = document.getElementById("updateTargetError")

    habitForm.addEventListener("submit", (e) => {
        let valid = true;
        if (habitTitleInput.value.trim() === "") {
            habitTitleError.style.display = "block";
            valid = false;
        }
        if (habitTarget.value < 1) {
            habitTargetError.style.display = "block"
            valid = false
        }
        if (!valid) {
            e.preventDefault();
        }
    });

    habitFormUpdate.addEventListener("submit", (e) => {
        let valid = true;
        if (habitTitleUpdateInput.value.trim() === "") {
            habitTitleUpdateError.style.display = "block";
            valid = false;
            console.log(valid)
        }
        if (habitUpdateTarget.value < 1) {
            habitUpdateTargetError.style.display = "block"
            valid = false
        }
        if (!valid) {
            e.preventDefault();
        }
    });

    //Open update habit modal
    window.openHabitUpdateModal = (habitId) => {
        fetch("/habits/" + habitId)
            .then(response => response.json())
            .then(habit => {
                document.getElementById("upHabitId").value = habitId
                document.getElementById("habitUpdateTitle").value = habit.title;
                document.getElementById("upDescription").value = habit.description;
                document.getElementById("upDifficulty").value = habit.difficulty;
                document.getElementById("upType").value = habit.type;
                document.getElementById("upTarget").value = habit.targetCount;
                document.getElementById("UpChallenge").value = habit.challengeId === null ? '' : habit.challengeId;
                document.getElementById("deleteHabit").setAttribute("href", "/habits/delete/" + habitId);

                const updateHabitModal = new bootstrap.Modal(document.getElementById("updateHabit"))
                updateHabitModal.show()
            })
            .catch(error => {
                console.error('Lỗi khi lấy dữ liệu phần thưởng:', error);
                alert('Không thể lấy dữ liệu phần thưởng.');
            });
    }

    // Xử lý nút cộng
    document.querySelectorAll('.plus-button').forEach(function (btn) {
        btn.addEventListener('click', function () {
            const countEl = this.nextElementSibling;
            let count = parseInt(countEl.textContent.replace('+', '')) || 0;
            countEl.textContent = '+' + (count + 1);
        });
    });

    // Xử lý nút trừ
    document.querySelectorAll('.minus-button').forEach(function (btn) {
        btn.addEventListener('click', function () {
            const countEl = this.previousElementSibling;
            let count = parseInt(countEl.textContent.replace('+', '')) || 0;
            countEl.textContent = '+' + (count - 1);
        });
    });

    // Daily
    function toggleRepeatOptions() {
        const repeatFrequency = document.getElementById('repeatFrequency').value;
        const repeatEverySection = document.getElementById('repeatEverySection');
        const repeatDaysSection = document.getElementById('repeatDaysSection');
        const repeatMonthDaysSection = document.getElementById('repeatMonthDaysSection');
        const repeatEveryUnit = document.getElementById('repeatEveryUnit');

        repeatEverySection.style.display = 'none'
        repeatDaysSection.style.display = 'none';
        repeatMonthDaysSection.style.display = 'none';

        if (repeatFrequency === 'WEEKLY') {
            repeatEverySection.style.display = 'block';
            repeatDaysSection.style.display = 'block';
            repeatEveryUnit.textContent = 'Weeks';
        } else if (repeatFrequency === 'MONTHLY') {
            repeatEverySection.style.display = 'block';
            repeatMonthDaysSection.style.display = 'block';
            repeatEveryUnit.textContent = 'Months';
        }
    }


    const form = document.getElementById('dailyForm');
    const modalTitle = document.getElementById('modalTitle');
    const dailyId = document.getElementById('dailyId').value;

    if (dailyId && dailyId !== '') {
        form.action = '/dailies/update';
        modalTitle.textContent = 'Chỉnh sửa';
    } else {
        form.action = '/dailies/create';
        modalTitle.textContent = 'Tạo mới';
    }

    document.getElementById('task-detail').addEventListener('click', (event) => {
        const dailyId = event.target.getAttribute('data-daily-id')
        fetch("/dailies/update/" + dailyId)
            .then(response => response.json())
            .then(daily => {
                console.log(daily)
                // Điền dữ liệu vào các trường trong modal
                document.getElementById("dailyId").value = dailyId || '';
                console.log("ok");
                document.getElementById("dailyTitle").value = daily.title || '';
                document.getElementById("dailyDes").value = daily.description || '';
                // Difficulty
                document.getElementById("dailyDifficulty").value = daily.difficulty || '';
                // Repeat Frequency
                document.getElementById("repeatFrequency").value = daily.repeatFrequency || 'DAILY';
                toggleRepeatOptions(); // Gọi lại để hiển thị/ẩn các section phù hợp
                // Repeat Every
                document.getElementById("repeatEvery").value = daily.repeatEvery || 1;

                // Repeat Days (Weekly)
                if (daily.repeatDays) {
                    daily.repeatDays.forEach(day => {
                        const checkbox = document.querySelector(`input[value="${day}"]`);
                        if (checkbox) {
                            checkbox.checked = true;
                        }
                    });
                } else {
                    // Reset tất cả checkbox nếu không có dữ liệu
                    document.querySelectorAll('#repeatDaysSection input[type="checkbox"]').forEach(checkbox => {
                        checkbox.checked = false;
                    });
                }

                // Repeat Month Days (Monthly)
                if (daily.repeatMonthDays) {
                    daily.repeatMonthDays.forEach(day => {
                        const checkbox = document.querySelector(`input[value="${day}"][id^="month-day-"]`);
                        if (checkbox) {
                            checkbox.checked = true;
                        }
                    });
                } else {
                    // Reset tất cả checkbox nếu không có dữ liệu
                    document.querySelectorAll('#repeatMonthDaysSection input[type="checkbox"]').forEach(checkbox => {
                        checkbox.checked = false;
                    });
                }

                // Challenge
                document.getElementById("challenge").value = daily.challengeId || '';

                // Mở modal
                const modal = new bootstrap.Modal(document.getElementById('modalDailyCreate'));
                modal.show();
            })
            .catch(error => {
                console.error('Lỗi khi lấy dữ liệu Daily:', error);
                alert('Không thể tải dữ liệu Daily. Vui lòng thử lại.');
            });


    });

    // Checkbox daily toggle
    document.querySelectorAll('.daily-checkbox').forEach(function (checkbox) {
        checkbox.addEventListener('click', function () {
            const item = this.closest('.checkbox-item');
            item.classList.toggle('completed');

            if (item.classList.contains('completed')) {
                this.innerHTML = '<i class="fas fa-check"></i>';
            } else {
                this.innerHTML = '';
            }
        });
    });

    // Mở rộng nhật ký
    document.querySelectorAll('.journal-day').forEach(function (journal) {
        journal.addEventListener('click', function () {
            this.classList.toggle('expanded');
            const icon = this.querySelector('i');
            if (this.classList.contains('expanded')) {
                icon.classList.remove('fa-chevron-down');
                icon.classList.add('fa-chevron-up');
            } else {
                icon.classList.remove('fa-chevron-up');
                icon.classList.add('fa-chevron-down');
            }
        });
    });
});

