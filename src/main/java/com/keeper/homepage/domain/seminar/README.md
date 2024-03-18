## Seminar

### `SeminarService`에서 주의할 점

`validCloseTime` 메서드에 의해 입력 값이 `null`인지 체크하고 있습니다.

`Optional`을 사용하여 유연하게 코드를 작성할 수 있다고 생각이 들겠지만, 현재 프로그램이 동작되는 구조는 세미나가 생성되었을 때 마감
시간(`attendanceCloseTime`, `latenessCloseTime`) 값은 `null`인 상태로 생성되고 세미나 시작 기능에서 마감 시간을 설정하고 있습니다.

이때 `validCloseTime` 메서드가 동작하는데 둘 다 `null`일 때는 세미나가 시작되지 않았음을 표현할 수 있고, 둘 중에 하나만 `null`일 때는 시간 검증
로직에서 오류가 발생하기 때문에 비정상적인 값이라고 판단되어 예외 처리를 하였습니다.

최종적으로 `validCloseTime`에 의해 검증이 끝난 후 세미나가 시작됩니다.

해당 메서드를 보시고 더 효율적이고 좋은 방법이 있다면, 언제든지 톡 보내주시면 좋겠습니다!

### `SeminarRepository`의 `findByAvailableCloseTime` 동작 방식

`attendanceCloseTime`과 `latenessCloseTime`이 `null`이 아니고 지각 마감 시간(`latenessCloseTime`)이 현재 시간보다 이전인
세미나만 가져오는 기능입니다.

DB에 저장된 `virtual_seminar`는 마감 시간들이 `null`이 아닌 과거 시간이 들어있으므로 단순히 시간만 확인하면 됩니다. (`virtual_seminar`는
과거로 인식하기 때문에 조회 대상이 아닙니다.)

### `SeminarControllerTest`의 `이용 가능한 세미나 조회 테스트`에서 API를 사용하지 않는 테스트가 있는 이유

`현재 시간이 지각 마감 시간을 지난 경우 조회 가능한 세미나가 존재하지 않는다.`를 테스트할 때 과거 시간으로 저장된 세미나를 생성해야 하는데, `Controller`에서 과거
시간의 입력을 막아놨기 때문에 `SeminarRepository`로 생성하였습니다.

### `SeminarControllerTest`의 `세미나 조회 테스트`에서 모든 데이터를 검증하지 않는 이유

세미나가 생성되기 전의 개수인 `beforeLength`와 생성된 후의 개수인 `afterLength`를 통해 정상적으로 생성되었음을 확인할 수 있습니다.

지금까지 작성한 테스트 코드가 모두 동작한다는 가정하에 데이터들이 온전히 들어갔다고 판단하여 제일 최근에 들어간 데이터들만 검증하는 코드를 작성하였습니다.
