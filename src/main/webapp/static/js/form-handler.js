const FormHandler = (function() {
  function handleFormSubmit() {
    $(document).on('submit', 'form', function(event) {
      event.preventDefault();
      const form = this;
      const data = new FormData(form);
      
      $.ajax({
        type: 'POST',
        enctype: 'multipart/form-data',
        url: $(form).attr('action'),
        data: data,
        processData: false,
        contentType: false
      }).done(function(html) {
        $('#feed').html(html);
      });
    });
  }

  return {
    init: function() {
      handleFormSubmit();
    }
  };
})();
